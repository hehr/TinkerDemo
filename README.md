# TinkerDemo

  1.git clone https://github.com/Tencent/tinker.git
  
  2.Demo用于演示如何接入Tinker

## what's Tinker 
由腾讯开源的一款android平台上的热修补方案，见[Tinker](https://github.com/Tencent/tinker)

## this demo fix what?

  1.作者的readme写的不够详细，接入需要花一定的成本。

  2.在此就不吐槽国内的各大科技论坛了，如神马csdn，51cto之类的，博客内容千篇一律，错误多多。
  
  3.也做一个记录，方便其他童鞋接入此框架。
  
## step

- 添加依赖

  1.project  build.gradle
  
  ```
  classpath ('com.tencent.tinker:tinker-patch-gradle-plugin:1.7.1')//添加tinker依赖
  ```
  2.app  build.gradle
  
  ```
  compile "com.android.support:multidex:1.0.1"
  compile('com.tencent.tinker:tinker-android-anno:1.7.1') //可选，用于生成application类
  compile('com.tencent.tinker:tinker-android-lib:1.7.1') //tinker的核心库
  ```
  
- add gradle module 

  1.add plugin
  ```
  apply plugin: 'com.tencent.tinker.patch'

  ```
  2.add Tinker conf
  ```
  def bakPath = file("${buildDir}/tinkerFile/") //补丁包存放位置

ext {
    tinkerEnabled = true
    tinkerOldApkPath = "${bakPath}/app-debug-1114-14-20-32.apk" //每次打包需要修改该路径
    tinkerApplyMappingPath = "${bakPath}/"
    tinkerApplyResourcePath = "${bakPath}/app-debug-1103-18-13-12-R.txt"
}

def getOldApkPath() {
    return ext.tinkerOldApkPath
}
def getApplyMappingPath() {
    return ext.tinkerApplyMappingPath
}
def getApplyResourceMappingPath() {
    return  ext.tinkerApplyResourcePath
}

if (ext.tinkerEnabled) {
    tinkerPatch {
        oldApk = getOldApkPath()
        ignoreWarning = false
        useSign = true

        packageConfig {
            configField("patchMessage", "tinker is sample to use")
            configField("platform", "all")
        }

        lib {
            pattern = ["lib/armeabi/*.so"]
        }

        res {
            pattern = ["res/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
            ignoreChange = ["assets/sample_meta.txt"]
            largeModSize = 100
        }

        sevenZip {
            zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
        }

        dex {

            dexMode = "jar"
            pattern = ["classes*.dex",
                       "assets/secondary-dex-?.jar"]
            loader = ["com.tencent.tinker.loader.*",
                      "com.tencent.tinker.*",
                      "com.example.administrator.tinker_test.MyApplication"
            ]
        }

        buildConfig{
            tinkerId = "1.0"
            applyMapping = getApplyMappingPath()
            applyResourceMapping = getApplyResourceMappingPath()
        }
    }
}
  ```
  
  3.add tinker task 
  ```
  android.applicationVariants.all { variant ->
    /**
     * task type, you want to bak
     */
    def taskName = variant.name

    tasks.all {
        if ("assemble${taskName.capitalize()}".equalsIgnoreCase(it.name)) {
            it.doLast {
                copy {
                    def date = new Date().format("MMdd-HH-mm-ss")
                    from "${buildDir}/outputs/apk/${project.getName()}-${taskName}.apk"
                    into bakPath
                    rename { String fileName ->
                        fileName.replace("${project.getName()}-${taskName}.apk", "${project.getName()}-${taskName}-${date}.apk")
                    }

                    from "${buildDir}/outputs/mapping/${taskName}/mapping.txt"
                    into bakPath
                    rename { String fileName ->
                        fileName.replace("mapping.txt", "${project.getName()}-${taskName}-${date}-mapping.txt")
                    }

                    from "${buildDir}/intermediates/symbols/${taskName}/R.txt"
                    into bakPath
                    rename { String fileName ->
                        fileName.replace("R.txt", "${project.getName()}-${taskName}-${date}-R.txt")
                    }
                }
            }
        }
    }
}
  ```
- add code

  1.add application
  
  ```
  /**
 * Created by hehr on 16-11-11.
 */
@DefaultLifeCycle(
        application = "tinkerdemo.application",        //配置application
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)

public class TDApplication extends DefaultApplicationLike {

    public TDApplication(Application application,
                         int tinkerFlags,
                         boolean tinkerLoadVerifyFlag,
                         long applicationStartElapsedTime,
                         long applicationStartMillisTime,
                         Intent tinkerResultIntent,
                         Resources[] resources,
                         ClassLoader[] classLoader,
                         AssetManager[] assetManager) {

        super(application,
                tinkerFlags,
                tinkerLoadVerifyFlag,
                applicationStartElapsedTime,
                applicationStartMillisTime,
                tinkerResultIntent,
                resources,
                classLoader,
                assetManager);


    }


    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        MultiDex.install(base);
        TinkerInstaller.install(this);

    }
}

  ```
  2.fix manifests.xml
  
  此处的app name需要和上面的application注入配置保持一致
  
  ```
   android:name="tinkerdemo.application"

  ```
  
  添加权限
  
  ```
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
  ```
  
  3.add patch code
  
  ```
  String patchPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.zip"; //差分文件后缀民可以任意，不推荐使用.apk文件
                File file = new File(patchPath);

                if (file.exists()){

                    Log.v(TAG,"补丁文件存在");
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);

                }
  ```

## use

  1.关闭 android studio instant run
  
  2.fix bug
  
  3.修改build.gradle的tinkerOldApkPath配置项，改为当前有bug的文件，在app/build/tinkerFile/app-debug-xxxx.apk
  
  4.项目路径下 Terminal run: gradle tinkerPatchDebug 
  
  5.生成差分文件，在app/build/outputs/tinkerPath/patch_signed_7zip.apk （改名.zip，实验发现tinker不区分文件后缀）就是我们需要的差分文件，将此文件push到/sdcard/，触发打补丁代码，即可。
  
