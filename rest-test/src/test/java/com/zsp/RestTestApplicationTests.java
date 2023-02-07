package com.zsp;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.task.Task;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class RestTestApplicationTests {
    //    使用activity工作流先生成数据库表：23张
    @Test
    void testCreateTable() {
//        使用xml生成activity工作流
        Logger logger = LoggerFactory.getLogger(RestTestApplicationTests.class);
//        获取activity提供的工具类
//        获取这个默认engine时就会自动创建数据库表
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

        System.out.println(engine);

    }

    /**
     * 1、部署流程
     * 2、启动流程实例
     * 3、请假人发出请假申请
     * 4、班主任查看任务
     * 5、班主任审批
     * 6、最终的教务处Boss审批
     */
// 1、部署流程
    @Test
    public void createActivityTask() {
        //获取默认的流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getRepositoryService()//得到库服务
                .createDeployment()//创建部署
                .name("askRest")
                .addClasspathResource("bmp/testmy.bpmn20.xml") //导入流程文件!!!!一定要加bpmn20
                .addClasspathResource("bmp/testmy.bpmn20.png")   //导入流程图  !!!!一定要加bpmn20
                .deploy();   //开始部署
    }/*输出结果：
  2023-02-07 19:11:48.019  INFO 30708 --- [           main] org.activiti.engine.ProcessEngines       : Initializing process engine using configuration 'file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml'
2023-02-07 19:11:48.021  INFO 30708 --- [           main] org.activiti.engine.ProcessEngines       : initializing process engine for resource file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml
2023-02-07 19:11:49.147  INFO 30708 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
2023-02-07 19:11:49.154  INFO 30708 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default
2023-02-07 19:11:49.284  INFO 30708 --- [           main] o.a.e.impl.bpmn.deployer.BpmnDeployer    : Process deployed: {id: Test:2:50004, key: Test, name: Test }
    */

    //2、启用创建的流程图进行审批
    @Test
    public void testStartProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //act_re_procdef表里的id，如果没生成，去看上一步，真是哔了狗
        processEngine.getRuntimeService()
                .startProcessInstanceById("Test:2:50004");
    }/*
    如果id不对，会报错no deployed process definition found with id 'myProcess_1:1:4'
    这里获取上一步deploy的 结果 {id: Test:1:4, key: Test, name: Test }
    我们改为 Test:1:$ 之后，
    2023-02-06 15:44:05.004  INFO 36724 --- [           main] org.activiti.engine.ProcessEngines       : Initializing process engine using configuration 'file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml'
    2023-02-06 15:44:05.005  INFO 36724 --- [           main] org.activiti.engine.ProcessEngines       : initializing process engine for resource file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml
    2023-02-06 15:44:06.023  INFO 36724 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
    2023-02-06 15:44:06.028  INFO 36724 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default
    个人实验执行了两次：
    这里使用第1列 id
    2505	1	2502	2501	Test:1:4	student leave			学生请假	sid-83f70fa4-a638-443a-9452-e5ceced3e708				50	2023-02-06 15:44:09.939			1
    5005	1	5002	5001	Test:1:4	student leave			学生请假	sid-83f70fa4-a638-443a-9452-e5ceced3e708				50	2023-02-06 15:46:36.962			1
    */

    //3、请假人发出请假申请
    @Test
    public void testAsk() {
//        获取activity7的引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//    查看act_ru_task表，然后把id给上，发起请假申请
        // 可选项为： 2505，5005
        processEngine.getTaskService().complete("2505");

    }/*
    2023-02-06 16:22:01.738  INFO 34648 --- [           main] org.activiti.engine.ProcessEngines       : Initializing process engine using configuration 'file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml'
    2023-02-06 16:22:01.739  INFO 34648 --- [           main] org.activiti.engine.ProcessEngines       : initializing process engine for resource file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml
    2023-02-06 16:22:02.881  INFO 34648 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
    2023-02-06 16:22:02.886  INFO 34648 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default

    */

    //    4、老师查看请假要求
    @Test
    public void queryTask() {
//        获得流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//    创造搜索，下面的teacher是我们在创建bpmn的时候写的assignee值
        List<Task> student = engine.getTaskService().createTaskQuery()
//                .taskAssignee("office")
                .list();
        for (Task task : student) {
            System.out.println(task);
        }
    }/*
    执行结果
  2023-02-06 16:33:57.529  INFO 12444 --- [           main] org.activiti.engine.ProcessEngines       : Initializing process engine using configuration 'file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml'
    2023-02-06 16:33:57.530  INFO 12444 --- [           main] org.activiti.engine.ProcessEngines       : initializing process engine for resource file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml
    2023-02-06 16:33:58.571  INFO 12444 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
    2023-02-06 16:33:58.577  INFO 12444 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default
    Task[id=5005, name=student leave]
    Task[id=7502, name=teacher Event]
    */

    //5、班主任审批
    @Test
    public void testFinishTask_manager() {
//        获取流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//    这里的id是上面查询生成的请假申请id
        //查看act_ru_task表，或者通过上面查询获得
        engine.getTaskService()
                .complete("7502");
    }/*
    2023-02-06 16:47:25.621  INFO 37156 --- [           main] org.activiti.engine.ProcessEngines       : Initializing process engine using configuration 'file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml'
    2023-02-06 16:47:25.622  INFO 37156 --- [           main] org.activiti.engine.ProcessEngines       : initializing process engine for resource file:/E:/adongstack/project/selfUp/ActivityStudy/rest-test/target/classes/activiti.cfg.xml
    2023-02-06 16:47:26.624  INFO 37156 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
    2023-02-06 16:47:26.630  INFO 37156 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default
    */


}
