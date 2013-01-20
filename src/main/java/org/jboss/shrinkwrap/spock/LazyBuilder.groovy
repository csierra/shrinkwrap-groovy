package org.jboss.shrinkwrap.spock

import org.jboss.shrinkwrap.api.Archive

public class LazyBuilder {
		
	def Archive<?> instance;
	def closures = []
	
	def LazyBuilder(Archive<?> instance) {
		this.instance = instance;
	}
	
	def appendClosures(Closure<?> ... c) {
		c.each {
			it.delegate = this
			this.closures.add(it)
		}
	}
	
	def classes(Class<?>[] classes) {
		this.instance.addClasses(classes)
	}
	
	def libraries(Archive<?> ... archives) {
		this.instance.addAsLibraries(archives)
	}
	
	def packageFrom(Class<?> k) {
		this.instance.addPackage(k.getPackage())
	}
	
	def packages(String ... packages) {
		packages.each {p -> this.instance.addPackages(true, p) }
	}
	
	def packagesFrom(Class<?> ... classes) {
		classes.each { this.instance.addPackage(it.getPackage()) }
	}
	
	def asModule(module) {
		if (module instanceof LazyBuilder) {
			module = module.build()
		}
		this.instance.addAsModule(module)
	}
	
	def invokeMethod(String name, args) {
		def m = name.capitalize();
		args = args.collect {
			if (it instanceof LazyBuilder) {
				return it.build()
			}
			else {
				return it
			}
		}
		def method = this.instance.metaClass.getMetaMethod("add$m", args)
		if (method == null) {
			method = this.instance.metaClass.getMetaMethod("$name", args)
		}
		method.invoke(this.instance, args)
	}
	
	def include(Closure<?> i) {
		i.delegate = this
		i()
	}
	
	def build() {
		this.closures.each {
			it()
		}
		return this.instance
	}
}
