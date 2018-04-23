---
layout: post
title: Freemarker基本使用
category: Java
keywords: Freemarker
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


## Freemarker

导入Freemarker的jar包

## 生成Java代码

	/**
	 * 生成Java代码
	 * */
	public static void generateJavaCode(){
		

		String templateFile = "src/demo_java.ftl";
		String targetFile = "src/com/luofeng/Person.java";
		
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("packageName", "com.luofeng");
		map.put("className", "Person");
		map.put("author", "luofeng");

		
        List<Attribute> attr_list = new ArrayList<Attribute>();
        attr_list.add(new Attribute("id", "Long"));
        attr_list.add(new Attribute("name", "String"));
        attr_list.add(new Attribute("age", "Integer"));
        attr_list.add(new Attribute("hobby", "List<String>"));

        map.put("attrs", attr_list);
        
        Configuration configuration = new Configuration();
		
		try {
			Template template = configuration.getTemplate(templateFile);
			template.setEncoding("UTF-8");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));  
            template.process(map, out);
            out.flush();
            out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
        
	}

Java的ftl模板文件

	package ${packageName};
	
	import java.util.List;
	
	/**
	 *  @author ${author}
	 */
	public class ${className} {
	    <#list attrs as attr> 
	    private ${attr.value!"meizhi"} ${attr.name};
	    </#list>
	
	    <#list attrs as attr>
	    public void set${attr.name?cap_first}(${attr.value!"meizhi"} ${attr.name}){
	        this.${attr.name} = ${attr.name};
	    }
	    public ${attr.value!"meizhi"} get${attr.name?cap_first}(){
	        return this.${attr.name};
	    }
	
	    </#list>
	}

其中

	<h1>Welcome ${user!"Anonymous"}!</h1>

可以为替换的变量设置默认值，因为变量为空会直接崩溃掉

	<#if user??><h1>Welcome ${user}!</h1></#if>

可以判断变量是否存在，不存在的话会忽略掉整个代码段


## 生成Html代码

	/**
	 * 生成Html代码
	 * */
	public void generateHtmlCode(){
		String templateFile = "src/demo_html.ftl";
		String targetFile = "src/html.html";
		
		Map map = new HashMap<String, String>();
		map.put("message", "hello world");
		map.put("name", "java小强");
		
		Configuration configuration = new Configuration();
		
		try {
			Template template = configuration.getTemplate(templateFile);
			template.setEncoding("UTF-8");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));  
            template.process(map, out);
            out.flush();
            out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}


对应的ftl模板

	<html>  
	  <head>  
	        <title>freemarker是什么</title>  
	    </head>  
	    <body>  
	        <h1>${message},${name}</h1>  
	    </body>  
	</html>

需要注意的是，在代码中我设置的编码格式是UTF-8,但是eclipse默认打开文件的编码格式可能是gbk,如果这个全局值不修改，直接打开生成的文件就会乱码，位置是在：

	window->Preferences->General->Workspace->Text file encoding