package org.jruby.anno;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jruby.Component;
import org.jruby.RubyModule.MethodClumper;
import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
import org.jruby.util.JRubyClassLoader;
import org.jruby.util.log.Logger;
import org.jruby.util.log.LoggerFactory;

public class InvokerGenerator {

    private static final Logger LOG = LoggerFactory.getLogger("InvokerGenerator");

    public static final boolean DEBUG = false;
    
    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr);
        
        List<String> classNames = new ArrayList<String>();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                classNames.add(line);
            }
        } finally {
            br.close();
        }

        DumpingInvocationMethodFactory dumper = new DumpingInvocationMethodFactory(args[1], new JRubyClassLoader(ClassLoader.getSystemClassLoader()));

        for (String name : classNames) {
            MethodClumper clumper = new MethodClumper();
            
            try {
                if (DEBUG) LOG.debug("generating for class {}", name);
                Class cls = Class.forName(name, false, InvokerGenerator.class.getClassLoader());
                if(isClassComponentEnabled(cls)) {

                    clumper.clump(cls);

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }

                    for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
                        dumper.getAnnotatedMethodClass(entry.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private static boolean isClassComponentEnabled(Class cls) {
        String disabledComponents = System.getProperty("jruby.disabled.components", "IO");
        if (cls.isAnnotationPresent(JRubyClass.class)) {
            Component[] components = ((JRubyClass) cls.getAnnotation(JRubyClass.class)).components();
            for (Component component : components) {
                if (disabledComponents.matches(component.name())) {
                    LOG.debug("skipping invoker generation for {}: disabled component", cls.getSimpleName());
                    return false;
                }
            }
        }
        return true;
    }
}
