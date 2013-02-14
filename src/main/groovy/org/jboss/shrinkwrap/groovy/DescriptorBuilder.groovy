package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.asset.Asset
import org.jboss.shrinkwrap.api.asset.StringAsset
import org.jboss.shrinkwrap.descriptor.api.Descriptor

/**
 * Builder class that delegates method class to the underlying descriptor implementation. 
 * First it looks for an exact match. If no one is found then it looks for a method named create${Name}.
 * If still no one is found the it looks for a method named getOrCreate${$Name}.
 * It also nests closures properly.
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
class DescriptorBuilder extends LazyBuilder<Descriptor> {
	
	DescriptorBuilder(instance) {
		super(instance)
	}
	
	def asType(Class<?> type) {
		this.build()
		if (type == Asset) 
			return new StringAsset(this.instance.exportAsString())
		if (type == NamedAsset)
			return new NamedAsset([this as Asset, this.instance.descriptorName])
	}
	
	def methodMissing(String name, args) {
		try {
			def realargs = args
			def nested = false
			def Closure<?> closure
			if (args[-1] instanceof Closure) { //We have a nested closure
				closure = args[-1].clone()
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
				closure.resolveStrategy = Closure.DELEGATE_FIRST
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
