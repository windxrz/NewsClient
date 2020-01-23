# 去年头条

## APP简介 

### 主页面 Home
	
根据新闻分类展示最新的新闻列表。

点击每条新闻的右下角图标可以收藏、离线下载，或取消。也可长按唤出菜单。

支持上拉刷新、下拉获取更多。
	
### 搜索页 Search

关键词搜索新闻。

### 账户页 Account
	
点按顶部两个按钮，查看收藏列表和离线列表。

点击Settings进入设置页，可设置项有：
		
 - 夜间模式 开/关
 - 显示图片 开/关
 - 增/删在主页面展示的新闻分类

### 新闻详情页

右上角唤出菜单，功能有：

收藏、离线、分享、文字转语音、夜间模式切换。

正文人名/地名/组织名可链接到百度百科。

支持长按复制。

###  源码各个包内容简介

com.java.group6 下有 3 个包：controller, model, ui

 - controller包
 
Operation类，封装对新闻数据的各种操作，包括获取、收藏、离线、分享等

MyApplication类：程序入口。

NewRequester等其余类：使用Retrofit框架实现对新闻API的HTTP请求功能。
	
 - model包
	
NewsBrief类：一条新闻的简要信息

NewsBriefList类：新闻简要信息列表

NewsDetail类：一条新闻的详细信息

 - ui包
	
所有关于UI的类
