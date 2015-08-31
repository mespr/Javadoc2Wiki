// Javadoc2Wiki - original author: Michael Sprague
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
    
package javadoc2wiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;

public class WikiDoclet {
	static String packageName;
    public static boolean start(RootDoc root) {

        for (ClassDoc cls : root.classes()) {
        	if (!cls.isOrdinaryClass()) continue;
        	if (!cls.isPublic()) continue;
        	BufferedWriter out = null;
        	try  
        	{
        	    FileWriter fstream = new FileWriter(makeWikiName(cls.name())+".txt");
        	    out = new BufferedWriter(fstream);
        	    
        	    // Wiki header. TOPICPARENT is set to package name and assumed to be created manually.
        	    // We could generate it from info found in package-info.java, but I would prefer
        	    // the flexibility of twiki markup for providing an overview
        	    packageName = makeWikiName(cls.containingPackage().name());
        	    out.write("%META:TOPICPARENT{name=\""+packageName+"\"}%\n");
        	    out.write("%INCLUDE{WebPageHeader}%\n");
        	    TocWriter.add(cls,cls.name(),0,makeWikiName(cls.name()));
        	    out.write(cls.commentText()+"\n");

        	    if (cls.enumConstants().length > 0) {
            	    out.write("\n---++ Enum Constants\n");
        	    }
        	    TocWriter.add(cls,"Summary of Fields",1);
        	    out.write("\n---++ Summary of Fields\n");
        	    out.write("|*Modifier and Type*|*Field*|\n");
        	    for (FieldDoc field : cls.fields()) {
        	    	out.write("| ="+field.modifiers()+" "+buildTypeName(field.type())+"= |="+field.name()+"= ");
        	    	if (!field.commentText().isEmpty()) {
        	    		out.write("<br>"+getFirstLine(field));
        	    	}
        	    	out.write("|\n");
        	    }

        	    TocWriter.add(cls,"Summary of Methods",1);
        	    out.write("\n---++ Summary of Methods\n");
        	    out.write("|*Modifier and Type*|*Method*|\n");
        	    for (MethodDoc method : cls.methods()) {
        	    	out.write("| ="+method.modifiers()+" "+buildTypeName(method.returnType())+"= | ="+buildMethodStatement(method)+"= ");
        	    	if (!method.commentText().isEmpty()) {
        	    		out.write("<br>"+getFirstLine(method));
        	    	}
        	    	out.write("|\n");
        	    }

        	    if (cls.innerClasses().length > 0) {
        	    	TocWriter.add(cls,"Inner Classes",1);
            	    out.write("\n---++ Inner Classes");
            	    for (ClassDoc icls : cls.innerClasses()) {
            	    	out.write("\n   * [["+makeWikiName(icls.name())+"]["+icls.name()+"]] - "+getFirstLine(icls));
            	    }
        	    }
        	    
        	    if (cls.constructors().length > 0) {
        	    	TocWriter.add(cls,"Constructors",1);
            	    out.write("\n---++ Constructors");
            	    for (ConstructorDoc constructor : cls.constructors()) {
            	    	out.write("\n=="+buildMethodStatement(constructor)+"==\n");
            	    	if (!constructor.commentText().isEmpty()) {
                	    	out.write("\n"+ constructor.commentText()+"\n");
            	    	}
            	    	if (constructor.paramTags().length > 0) {
    	        	    	out.write("|*Parameter*|*Description*|\n");
    	        	    	for (ParamTag param : constructor.paramTags()) {
    	        	    		out.write("|"+param.parameterName()+"|"+param.parameterComment().replace(System.getProperty("line.separator"),"")+"|\n");
    	        	    	}
            	    	}
            	    }
        	    }
        	    
        	    TocWriter.add(cls,"Method Details",1);
        	    out.write("\n---++ Method Details\n");
        	    for (MethodDoc method : cls.methods()) {
            	    TocWriter.add(cls,method.name()+"()",2);
        	    	out.write("\n---+++ "+method.name()+"()\n");
        	    	out.write("=="+method.modifiers()+" "+buildTypeName(method.returnType())+" "+buildMethodStatement(method)+"==\n");
        	    	if (!method.commentText().isEmpty()) {
            	    	out.write("\n"+method.commentText()+"\n");
        	    	}
        	    	if (method.paramTags().length > 0) {
	        	    	out.write("\n|*Parameter*|*Description*|\n");
	        	    	for (ParamTag param : method.paramTags()) {
	        	    		out.write("|"+param.parameterName()+"|"+param.parameterComment().replace(System.getProperty("line.separator"),"")+"|\n");
	        	    	}
        	    	}
        	    	for (Tag tag : method.tags("return")) {
        	    		String title = tag.name();
        	    		if (title.equals("@return")) {
	        	    		out.write("\n---++++ Return Value");
	        	    		out.write("\n"+tag.text());
        	    		} else {
        	    			out.write("\n---++++ "+tag.kind());
        	    			out.write("\n"+tag.text());
        	    		}
        	    	}
        	    }
        	    out.write("%META:FORM{name=\"WebForm\"}%\n");
        	    out.write("%META:FIELD{name=\"Title\" attributes=\"M\" title=\"Title\" value=\""+cls.name()+"\"}%\n");
        	    out.write("%META:FIELD{name=\"Description\" attributes=\"\" title=\"Description\" value=\""+getFirstLine(cls)+"\"}%\n");

        	    out.close();
          	}
        	catch (IOException e)
        	{
        	    System.err.println("Error: " + e.getMessage());
        	}

        }
        TocWriter.close();

        return true;
    }	
    
    private static String buildTypeName(Type type) {
    	String s="";
    	if (type.isPrimitive()) {
    		s+=type.typeName()+type.dimension();
    	} else if (type.qualifiedTypeName().matches("^java.*")) {
       		s+=type.typeName()+type.dimension();
    	} else {
    		s+="[["+makeWikiName(type.typeName())+"]["+type.typeName()+"]]"+type.dimension();
    	}
    	return s;
    }
    
    private static String buildMethodStatement(ExecutableMemberDoc method) {
    	String s = "";
    	s += method.name()+"(";
    	String params = "";
    	for (Parameter param : method.parameters()) {
    		params += buildTypeName(param.type())+" "+param.name()+",";
    	}
    	s += (params.length()>0?params.substring(0,params.length()-1):"")+")";
    	
    	return s;
    }
    
    private static String getFirstLine(Doc doc) {
    	String s = "";
    	Tag[] tags = doc.firstSentenceTags();
    	for (Tag tag : tags) {
    		s += tag.text();
    	}
    	return s.replace("\n","");
    }
    
    private static String makeWikiName(String s) {
    	String result = "";
    	String[] words = s.split("\\.");
    	for (String word : words) {
    		result += word.substring(0, 1).toUpperCase() + word.substring(1);
    	}
    	return result;
    }
    
    private static class TocWriter {
    	static HashMap<String, BufferedWriter> files = new HashMap<String, BufferedWriter>();

		static BufferedWriter getFile(ClassDoc cls) {
			BufferedWriter file = files.get(packageName);
	    	try {
	    		if (file == null) {
		    	    FileWriter tocstream = new FileWriter(packageName+"TOC.txt");
		    	    file = new BufferedWriter(tocstream);
		    	    files.put(packageName,file);
		    	    file.write("   * [["+packageName+"]["+cls.containingPackage().name()+"]]\n");
	    		}
	    	} catch (IOException e) {
	    		System.err.println("Error: " + e.getMessage());
	       	}
	    	return file;
    	}
		static void add(ClassDoc cls, String title, int level) {
			add(cls,title,level,makeWikiName(cls.name())+"#" + title.replace(" ", "_").replace("()",""));
		}
		static void add(ClassDoc cls, String title, int level, String link) {
			String line = "   "; //accommodate root package name
			for (int i=0;i<=level;i++) { // starting with level zero prefix three spaces
				line += "   ";
			}
			line += "* [["+link+"]["+title+"]]\n";
			BufferedWriter out = getFile(cls);
			try {
				out.write(line);
				out.flush();
	    	} catch (IOException e) {
	    		System.err.println("Error: " + e.getMessage());
	       	}
		}
		static void close() {
			try {
				for (BufferedWriter file : files.values()) {
				    file.close();
				}
	    	} catch (IOException e) {
	    		System.err.println("Error: " + e.getMessage());
	       	}
		}
    }
}
