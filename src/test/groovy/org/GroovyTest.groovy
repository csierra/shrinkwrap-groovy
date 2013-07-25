package org

class GroovyTest {
	
	def GroovyTest() {
		def a = new java.lang.String("Jonh")
		a.metaClass.mixin B
		
		a.sayHello()
		a.hello() //Resolves to sayHello through B.methodMissing
		a.with { //Shoudl resolve to sayHello but fails and methodMissing is not invoked
			hello()
		}
	}
	
	def static main(args) {
		new GroovyTest()
	}
	
}





