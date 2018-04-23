---
layout: post
title: Android HTTPS安全请求解析-keystore工具的使用
category: 网络技术
keywords: keystore,tomcat
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

# 一.keystore工具的使用(大部分摘自[使用Keytool工具生成证书及签名完整步骤](https://blog.csdn.net/sayyy/article/details/78351512))

	命令:

	 -certreq            生成证书请求
	 -changealias        更改条目的别名
	 -delete             删除条目
	 -exportcert         导出证书
	 -genkeypair         生成密钥对
	 -genseckey          生成密钥
	 -gencert            根据证书请求生成证书
	 -importcert         导入证书或证书链
	 -importpass         导入口令
	 -importkeystore     从其他密钥库导入一个或所有条目
	 -keypasswd          更改条目的密钥口令
	 -list               列出密钥库中的条目
	 -printcert          打印证书内容
	 -printcertreq       打印证书请求的内容
	 -printcrl           打印 CRL 文件的内容
	 -storepasswd        更改密钥库的存储口令

## 1.创建证书库(Keystore/JKS)及证书(Certificate):

	keytool -genkeypair \
        -alias www.mydomain.com \
        -keyalg RSA \
        –keysize 4096 \
        -keypass mypassword \
        -sigalg SHA256withRSA \
        -dname "cn=www.mydomain.com,ou=xxx,o=xxx,l=Beijing,st=Beijing,c=CN" \ 
        -validity 3650 \
        -keystore www.mydomain.com_keystore.jks \
        -storetype JKS \
        -storepass mypassword

使用**genkeypair**参数：

	查看选项keytool -genkeypair -option


* keytool 是jdk提供的工具，该工具名为”keytool“
* -alias www.mydomain.com 此处”www.mydomain.com“为别名，可以是任意字符，只要不提示错误即可。因一个证书库中可以存放多个证书，通过别名标识证书。
* -keyalg RSA 此处”RSA“为密钥的算法。可以选择的密钥算法有：RSA、DSA、EC。
* –keysize 4096 此处”4096“为密钥长度。keysize与keyalg默认对应关系： 
2048 (when using -genkeypair and -keyalg is “RSA”) 
1024 (when using -genkeypair and -keyalg is “DSA”) 
256 (when using -genkeypair and -keyalg is “EC”)
* -keypass mypassword 此处”mypassword “为本条目的密码(私钥的密码)。最好与storepass一致。
* -sigalg SHA256withRSA 此处”SHA256withRSA“为签名算法。keyalg=RSA时，签名算法有：MD5withRSA、SHA1withRSA、SHA256withRSA、SHA384withRSA、SHA512withRSA。keyalg=DSA时，签名算法有：SHA1withDSA、SHA256withDSA。此处需要注意：MD5和SHA1的签名算法已经不安全。
* -dname “cn=www.mydomain.com,ou=xxx,o=xxx,l=Beijing,st=Beijing,c=CN” 在此填写证书信息。”CN=名字与姓氏/域名,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码”
* -validity 3650 此处”3650“为证书有效期天数。
* -keystore www.mydomain.com_keystore.jks 此处”www.mydomain.com_keystore.jks“为密钥库的名称。此处也给出绝对路径。默认在当前目录创建证书库。
* -storetype JKS 此处”JKS “为证书库类型。可用的证书库类型为：JKS、PKCS12等。jdk9以前，默认为JKS。自jdk9开始，默认为PKCS12。
* -storepass mypassword 此处”mypassword “为证书库密码(私钥的密码)。最好与keypass 一致。

说明： 
上述命令，需要将 -dname 参数替换（尤其时域名要写对）、密码更改即可，其它可保持不变。


生成证书命令:

	keytool -genkey -alias luofeng -keyalg RSA -keystore .keystore


## 2.生成证书签名请求(CSR)

命令如下：

	keytool -certreq -keyalg RSA \
	        -alias www.mydomain.com \
	        -keystore www.mydomain.com_keystore.jks \
	        -storetype JKS \
	        -storepass mypassword \
	        -file www.mydomain.com_certreq.csr

**certreq**;

	查看具体参数选项:keytool -certreq -option

解释： 

* -file www.mydomain.com_certreq.csr 此处”www.mydomain.com_certreq.csr “为证书签名请求文件。

说明： 

将”www.mydomain.com_certreq.csr “文件发送给证书签名机构，然后等待证书签名机构将签名的证书发回，再进行下一步。

## 3.将签名请求发给证书签名机构

签名机构通过gencert来处理证书：

需要提供自己的签名请求文件，还需要签名机构的根证书(也是通过genkeypair生成的，只不过签名机构的证书被广泛认可)

## 3.将已签名的证书导入证书库

如果到了这步，应该会拿到两个证书。一个是签名机构的根证书（假定为GlobalSign_cert.cer），一个是www.mydomain.com的已签名证书（假定为www.mydomain.com_cert.cer）。两个证书均导入到证书库（www.mydomain.com_keystore.jks）中。

导入签名机构的根证书：

	keytool -import -trustcacerts \
	        -keystore www.mydomain.com_keystore.jks \
	        -storepass mypassword \
	        -alias root_GlobalSign \
	        -file GlobalSign_cert.cer

说明： 

alias和file两个参数进行替换。

导入www.mydomain.com的已签名证书

	keytool -import -trustcacerts \
	        -keystore www.mydomain.com_keystore.jks \
	        -storepass mypassword \
	        -alias www.mydomain.com \
	        -file www.mydomain.com_cert.cer
说明： 
alias参数要与生成时一致，file参数进行替换。

辅助命令

查看证书库

	keytool -list -v \
	        -keystore www.mydomain.com_keystore.jks \
	        -storepass mypassword

查看证书签名请求

	keytool -printcertreq  -file www.mydomain.com_certreq.csr

查看已签名证书

	keytool -printcert -file GlobalSign_cert.cer
	keytool -printcert -file www.mydomain.com_cert.cer

## 二. 证书概念的区别

CSR文件:CSR文件-Certificate Signing Request证书签名请求，这个文件是向权威机构获得颁发证书的请求，核心是一个公钥
JKS：java key store,java的keytool上实现的密钥库的格式
Cer：window下证书格式

如果不想要得到签名机构的认可，可以直接使用JKS文件

## 三. C/S证书交互配置


1. 生成服务端证书(库)（注意域名填入服务端网站域名，否则访问会弹出警告）

2. 导出服务端证书

	keytool -export -alias server -keystore 证书库路径+名称 -storepass 密码 -rfc -file 导出证书路径+名称.cer

tomcat中开启SSL

3. 修改Server.xml:

	<Connector port="8443" protocol="org.apache.coyote.http11.Http11Protocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" 
	keystoreFile="C:/Users/luofe/Desktop/keystore/server.jks" keystorePass="123456"/>

4. 注释掉:

	<!--<Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />-->

5. 安装证书到客户端浏览器(第二步导出的证书)

6. 在浏览器中访问tomcat通过https://127.0.0.1:8443/

如果出现不安全或是该链接不是私密连接的提示，说明证书安装的位置不对，这个证书只有安装到“受信任的根证书颁发机构”下才会被浏览器信任。

>运行中输入certmgr.msc可以打开window的证书管理器，查看证书的安装位置

	生成本地测试证书:

	keytool -genkeypair -alias localhost -keyalg RSA -keysize 4096 -keypass 123456 -sigalg SHA256withRSA -dname "cn=localhost,ou=test,o=test,l=Beijing,st=Beijing,c=CN" -validity 3650 -keystore server.jks -storetype JKS -storepass 123456
	
	导出本地测试
	
	keytool -export -alias localhost -keystore server.jks -storepass 123456 -rfc -file server.cer




