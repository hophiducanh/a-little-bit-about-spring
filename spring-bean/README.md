[Interviewer: What are the ways to inject beans into Spring container?
](https://blog.devgenius.io/interviewer-what-are-the-ways-to-inject-beans-into-spring-container-5c0984b2493d)

----
The **AnnotationConfigApplicationContext** is introduced in **Spring 3.0**.

Concept of **AnnotationConfigWebApplication** is replacing `xml` with `.java` class, by searching **@Configuration** annotated class.


First, let's see the **AnnotationConfigApplicationContext** class, which was introduced in Spring 3.0. It can take classes annotated with **@Configuration,** **@Component,** and JSR-330 metadata as **input**.

```
ApplicationContext context = new AnnotationConfigApplicationContext(AccountConfig.class);
AccountService accountService = context.getBean(AccountService.class);
```
----

https://www.concretepage.com/spring/example_annotationconfigapplicationcontext_spring

AppTest.java

```java
package com.concretepage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class AppTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 
		// Register one or more component classes to be processed.
		// Note that refresh() must be called in order for the context to fully process the new classes.
		ctx.register(AppConfig.class);
		
		//refresh to get bean.
		ctx.refresh();

		Entitlement ent= (Entitlement)ctx.getBean("entitlement");
	        System.out.println(ent.getName());		
	        System.out.println(ent.getTime());
	
	}
} 
```

AppConfig.java
```java
package com.concretepage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig  {
	@Bean(name="entitlement")
	public Entitlement entitlement(){
		Entitlement ent= new Entitlement();
		ent.setName("Entitlement");
		ent.setTime(20);
		return ent;
	}
} 
```

Entitlement.java
```java
package com.concretepage;
public class Entitlement {
	private String name;
	private int time;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
} 
```

## Spring bean life cycle
- https://medium.com/@wdn0612/spring-beans-from-born-to-death-d2a325d872d7
