## [What is the difference between BeanPostProcessor and init/destroy method in Spring?](https://stackoverflow.com/questions/9862127/what-is-the-difference-between-beanpostprocessor-and-init-destroy-method-in-spri)



---

1. ApplicationContextInitializer.initialize
   - https://reflectoring.io/spring-boot-application-events-explained/
   - https://habr.com/ru/company/jugru/blog/425333/
   - https://spring.io/blog/2020/03/27/dynamicpropertysource-in-spring-framework-5-2-5-and-spring-boot-2-2-6
   - https://rieckpil.de/override-spring-boot-configuration-properties-for-tests/
2. AbstractApplicationContext.refresh
3. BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry
4. BeanDefinitionRegistryPostProcessor.postProcessBeanFactory
5. InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation
6. SmartInstantiationAwareBeanPostProcessor.determineCandidateConstructors
7. MergedBeanDefinitionPostProcessor.postProcessorMergedBeanDefinition
8. InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation
9. SmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference
10. BeanFactoryAware.setBeanFactory
11. InstantiationAwareBeanPostProcessor.postProcessPropertyValues
12. ApplicationContextAwareProcessor.invokeAwareInterfaces
13. BeanNameAware.setBeanName
14. InstantiationAwareBeanPostProcessor.postProcessBeforeInitialization
15. @PostConstruct
16. InitiallzingBean.afterPropertiesSet
17. InstantiationAwareBeanPostProcessor.postProcessAfterInitialization
18. FactoryBean.getObject
19. SmartInitilallzingSingleton.afterSingletonInstantiated
20. CommondLineRunner.run
21. DisposableBean.destry

https://blog.csdn.net/qq_43141726/article/details/127263213