package javadoc2wiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class WikiDoclet {
    public static boolean start(RootDoc root) {
        for (ClassDoc cls : root.classes()) {
        	BufferedWriter out = null;
        	try  
        	{
        	    FileWriter fstream = new FileWriter("Class"+cls.name()+".txt"); //true tells to append data.
        	    out = new BufferedWriter(fstream);
        	    
        	    // Wiki header. TOPICPARENT is set to package name and assumed to be created manually.
        	    // We could generate it from info found in package-info.java, but I would prefer
        	    // the flexibility of twiki markup for providing an overview
        	    String packageName = cls.containingPackage().name();
        	    out.write("%META:TOPICPARENT{name=\""+packageName+"\"}%\n");
        	    out.write("---+"+cls.name()+"\n\n");
        	    out.write(cls.commentText()+"\n");
       	    
        	    out.write("\n---++ Summary of Fields\n");
        	    out.write("|*Modifier and Type*|*Field*|\n");
        	    for (FieldDoc field : cls.fields()) {
        	    	out.write("|"+field.modifiers()+" "+buildTypeName(field.type())+"|"+field.name());
        	    	if (!field.commentText().isEmpty()) {
        	    		out.write("<br>"+getFirstLine(field.commentText()));
        	    	}
        	    	out.write("|\n");
        	    }

        	    out.write("\n---++ Summary Methods\n");
        	    out.write("|*Modifier and Type*|*Method*|\n");
        	    for (MethodDoc method : cls.methods()) {
        	    	out.write("|"+method.modifiers()+" "+buildTypeName(method.returnType())+"|"+buildMethodStatement(method));
        	    	if (!method.commentText().isEmpty()) {
        	    		out.write("<br>"+getFirstLine(method.commentText()));
        	    	}
        	    	out.write("|\n");
        	    }

        	    out.write("\n---++ Method Details\n");
        	    for (MethodDoc method : cls.methods()) {
        	    	out.write("\n---+++ "+method.name()+"\n");
        	    	out.write("=="+method.modifiers()+" "+buildTypeName(method.returnType())+" "+buildMethodStatement(method)+"==\n\n");
        	    }

        	    out.close();
          	}
        	catch (IOException e)
        	{
        	    System.err.println("Error: " + e.getMessage());
        	}

        }

        return true;
    }	
    
    private static String buildTypeName(Type type) {
    	String s="";
    	if (type.isPrimitive()) {
    		s+=type.typeName()+type.dimension();
    	} else if (type.qualifiedTypeName().matches("^java.*")) {
       		s+=type.typeName()+type.dimension();
    	} else {
    		s+="[["+type.typeName()+"]]"+type.dimension();
    	}
    	return s;
    }
    
    private static String buildMethodStatement(MethodDoc method) {
    	String s = "";
    	s += method.name()+"(";
    	for (Parameter param : method.parameters()) {
    		s += buildTypeName(param.type())+" "+param.name()+",";
    	}
    	s += (s.length()>0?s.substring(0,s.length()-1):"")+")";
    	
    	return s;
    }
    
    private static String getFirstLine(String s) {
    	String lines[] = s.split(System.lineSeparator());
    	return lines[0];
    }
}
