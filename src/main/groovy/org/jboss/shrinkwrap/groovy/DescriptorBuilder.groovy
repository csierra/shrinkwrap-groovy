package org.jboss.shrinkwrap.groovy

import java.util.Map;

import org.codehaus.groovy.runtime.metaclass.MetaMethodIndex;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.spockframework.builder.ClosureBlueprint;

/**
 * Builder class that delegates method class to the underlying descriptor implementation. 
 * First it looks for an exact match. If no one is found then it looks for a method named create${Name}.
 * If still no one is found the it looks for a method named getOrCreate${$Name}.
 * It also nests closures properly.
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
class DescriptorBuilder extends LazyBuilder {
	
	
	DescriptorBuilder(instance) {
		super(instance)
	}
	
	def methodMissing(String name, args) {
		try {
			def realargs = args
			def nested = false
			def closure
			if (args[-1] instanceof Closure) { //We have a nested closure
				closure = args[-1]
				nested = true
				//Adjust the arguments removing the closure
				if (args.length <= 1) {
					realargs = null
				}
				else {
					realargs = args[0..-2]
				}
			}
			def metamethodname = name
			def metamethod = instance.metaClass.getMetaMethod(name, realargs)
			if (metamethod == null) {
				metamethodname = "create"+name.capitalize()
				metamethod = this.instance.metaClass.getMetaMethod(metamethodname, realargs);
			}
			if (metamethod == null) {
				metamethodname = "getOrCreate"+name.capitalize()
				metamethod = this.instance.metaClass.getMetaMethod(metamethodname, realargs);
			}
			if (metamethod == null) {
				throw new MissingMethodException(name, this.instance.class, args)
			}
			
			if (nested) { //We create a new builder to handle the nested closure
				metamethod = this.instance.metaClass.getMetaMethod(metamethodname, realargs);
				def child = metamethod.doMethodInvoke(this.instance, realargs)
				closure.delegate = new DescriptorBuilder(child)
				if (closure.owner instanceof Closure) {
					closure.@owner = closure.owner.owner
				}
				return closure() //return the result of the closure, not the closure itself
			}
			else {
				return metamethod.doMethodInvoke(instance, args)
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Could not invoke $name($args) on ${this.instance}", e)
		}
	}
}
