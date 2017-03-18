## WxRecyclerVier

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

##### 参考了别人的自定义上拉加载和下拉刷新，实现这个RecyclerView
	1.可选择只选上拉刷新或下拉刷新。
	2.直接传递LayoutId即可实现上下布局。
	3.可以实现SwipeRefreshLayout+下拉刷新。
##### 第一次使用Jcenter (good)，总结问题
	1.注册google邮箱可在QQ邮箱上注册。
#
	2.出现提醒错误要做工程和library同时添加
		lintOptions {
	        abortOnError false
	    }
#
	3.jcenter网页好像改了先建wangxuan/maven，再建包。
#
	4.先添加
	dependencies {
        classpath 'com.android.tools.build:gradle:2.2.2'
        classpath 'com.novoda:bintray-release:0.3.4' //tool
    } 
#
	5.下面和android{}同级
	publish {
	    userOrg = 'wangxuan' 				//用户名
	    groupId = 'com.wangxuan.library'	//包名相当于com.android.support
	    artifactId = 'wxrecyclerview'		//库名相当于appcompat-v7
	    publishVersion = '1.1'				//版本名相当于24.2.1
	    desc = 'Achieve refresh'			//备注
	    website = 'https://github.com/mochixuan/WxRecyclerView/RecyclerViewDemo/wxrecyclerview'	//随便写，最好github对应网址
	}
	对比如下
	compile 'com.wangxuan.library:wxrecyclerview:1.1'
	compile 'com.android.support:appcompat-v7:24.2.1'
#
	6.在Terminal 直接输入 修改username和key
	gradlew clean build bintrayUpload -PbintrayUser=username -PbintrayKey=key -PdryRun=false
#
	7.现在还没有add to jcenter 使用要在工程的build
	allprojects {
	    repositories {
	        jcenter()
	        maven {
	            url 'https://dl.bintray.com/wangxuan/maven/'
	        }
	    }
	}
	再在项目build
	compile 'com.wangxuan.library:wxrecyclerview:1.1'

#	
	8.添加到jcenter后才可以直接，时间大约2-3小时消失
	compile 'com.wangxuan.library:wxrecyclerview:1.1'
