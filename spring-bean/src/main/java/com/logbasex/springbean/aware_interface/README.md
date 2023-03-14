http://javainsimpleway.com/spring-aware-interfaces-for-beans/

Sometimes it is required that our beans needs to get some information about **Spring container** and its **resources**.

For example, sometime bean need to know the **current Application Context** using which it can perform some operations like loading specific bean from the container in a programmatic way.

So to make the beans aware about this, spring provides lot of **Aware** interfaces.

All we have to do is, make our bean to implement the **Aware** interface and implement the **setter** method of it.

**org.springframework.beans.factory.Aware** is the **root marker interface**.

All the **Aware** interfaces which we use are the **sub interfaces** of the **Aware** interface.

**Some of the commonly used Aware interfaces are**

**1) ApplicationContextAware**

Bean implementing this interface can get the current application context and this can be used to call any service from the application context

**2) BeanFactoryAware**
   
Bean implementing this interface can get the current bean factory and this can be used to call any service from the bean factory

**3) BeanNameAware**

Bean implementing this interface can get its name defined in the Spring container.

**4) MessageSourceAware**
   
Bean implementing this interface can get the access to message source object which is used to achieve internationalization

**5) ServletContextAware**
   
Bean implementing this interface can get the access to ServeltContext which is used to access servlet context parameters and attributes

**6) ServletConfigAware**

Bean implementing this interface can get the access to ServletConfig object which is used to get the servlet config parameters

**7) ApplicationEventPublisherAware**
   
Bean implementing this interface can publish the application events and we need to create listener which listen this event.

**8) ResourceLoaderAware**

Bean implementing this interface can load the resources from the classpath or any external file.