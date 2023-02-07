package com.zsp;

import com.alibaba.fastjson.JSON;
import com.zsp.pojo.ApprovalTemp;
import net.minidev.json.JSONArray;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xander
 * @version v1.0.0
 * @Package : com.zsp
 * @Description : 环境变量测试
 * @Create on : 2/7/2023 17:37
 **/
@SpringBootTest
public class ApprovalEvectionTest {

    /**
     * 部署流程
     * 这里要先用插件画出来
     */
    @Test
    public void createActivityTask() {
        //获取默认的流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()//得到库服务
                .createDeployment()//创建部署
                .name("MerchTest")
                .addClasspathResource("bmp/Approval.bpmn20.xml") //导入流程文件!!!!一定要加bpmn20
                .addClasspathResource("bmp/Approval.bpmn20.png")   //导入流程图  !!!!一定要加bpmn20
                .deploy();   //开始部署
    }/*输出结果：
2023-02-07 19:43:54.717  INFO 26356 --- [           main] o.a.e.impl.bpmn.deployer.BpmnDeployer    :
Process deployed: {id: Companyregistrationqualificationapproval:1:4, key: Companyregistrationqualificationapproval, name: Companyregistrationqualificationapproval }
    */

    /**
     * 启用创建的流程图进行审批
     * 这里使用部署生成的唯一ID
     * 开始一个流程使用此方法
     * @description 启用创建的流程图进行审批
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 7:44 PM
     */
    @Test
    public void testStartProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.err.println("启用创建的流程图进行审批");
        //act_re_procdef表里的id，如果没生成，去看上一步.
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById("Companyregistrationqualificationapproval:4:72504");

//      输出
        System.out.println("流程实例名称="+processInstance.getName());
        System.out.println("流程定义id=="+processInstance.getProcessDefinitionId());
    }/*输出结果：
2023-02-07 19:45:04.968  INFO 6780 --- [           main] o.a.engine.impl.ProcessEngineImpl        : ProcessEngine default created
2023-02-07 19:45:04.973  INFO 6780 --- [           main] org.activiti.engine.ProcessEngines       : initialised process engine default
    */

    /**
     * 商户进行审批流程
     * 此方法查看当前商户任务
     * 1. 查看自己当前的任务
     * 2. 商户在页面操作填写报表，然后提交任务
     * 3. 商户提交完成则进行提交，请看下一个单元测试
     */
    @Test
    public void testQueryMerchantProcess() {
//        获得流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//    创造搜索，查找商户的所有任务，但是实际上商户不需要看任务
        List<Task> merchs = engine.getTaskService().createTaskQuery()
                // 负责人是商户
                .taskAssignee("merchant")
                .list();
        System.err.println("当前商户的任务");
        for (Task task : merchs) {
            System.err.println(task);
        }
    }/*
    当前商户的任务
    Task[id=10008, name=Merchants submit approval information]
    Task[id=17505, name=Merchants submit approval information]

    如果商户提交任务，则此时会查不到任务，需要下一个处理人可以看到处理任务
    */

    /**
     * 商户提交审批
     * 根据查询的ID进行提交，比如这里为2505
     * 切记使用唯一ID
     * @description 商户提交审批
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 6:53 PM
     */
    @Test
    public void testMerchSubmitApproval() {
//        获取流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.err.println("商户完成手册之后，开始进行提交");
        TaskService taskService = engine.getTaskService();
        // 这里需要使用流转ID
        taskService
                .complete("75005");
    }/*运行结果：
    ProcessEngine default created
     initialised process engine default
    */

    /**
     *
     *  查询一级管理员的任务
     * @description 查询一级管理员的任务
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 7:15 PM
     */
    @Test
    public void testQueryApprovalLevel1() {
//        获得流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//    一级管理员的任务
        List<Task> merchs = engine.getTaskService().createTaskQuery()
                .taskAssignee("approvalLevel1")
                .list();
        System.err.println("当前一级审批的任务");
        for (Task task : merchs) {
            System.out.println(task);
        }
    }/*
    商户运行之后，可以看到结果
    这里提示为一级管理员审核
    Task[id=5002, name=First level approval]
    */

    /**
     * 商户审批，一级审核
     * 比较关键的一步，这里使用了流程变量
     * @description 商户审批，一级审核
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 7:14 PM
     */
    @Test
    public void testApprovalLevel1() {
//        获取流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        获取RunTimeService
        ApprovalTemp approvalTemp = new ApprovalTemp();
        // 如果不同意，则返回false，此时商户会看到自己的列表
        approvalTemp.setAgree("true");
        approvalTemp.setToNext("merchant");
        map.put("approval", approvalTemp);
        TaskService taskService = engine.getTaskService();
        Map<String, VariableInstance> variableInstances = taskService.getVariableInstances("77502");
        System.err.println("同意审批");
        System.err.println(variableInstances);
        taskService
                .complete("77502", map);
    }/*
    一级管理员提交审核
    */

    /**
     * 二级管理员的任务
     * @description 二级管理员的任务
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 8:24 PM
     */
    @Test
    public void testQueryApprovalLevel2() {
//        获得流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//    二级管理员的任务
        List<Task> merchs = engine.getTaskService().createTaskQuery()
                .taskAssignee("approvalLevel2")
                .list();
        System.err.println("当前二级审核的任务");
        for (Task task : merchs) {
            System.out.println(task);
        }
    }/*
    二级审批的任务：
    Task[id=80006, name=Secondary Approval]

    */

    /**
     * 二级管理员进行审批以及审核
     * 这里测试把任务转给商户
     * @description 二级管理员进行审批以及审核
     * @param
     * @return void
     * @author adong
     * @date 2/7/2023 8:26 PM
     */
    @Test
    public void testApprovalLevel2ToMerchant() {
//        获取流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        获取RunTimeService
        ApprovalTemp approvalTemp = new ApprovalTemp();
        // 如果不同意，则返回false，此时商户会看到自己的列表
        approvalTemp.setAgree("true");
        // 转给商户传 merchant ，转给上一个审核 approvalLevel1
        approvalTemp.setToNext("merchant");
        map.put("approval", approvalTemp);
        TaskService taskService = engine.getTaskService();
        Map<String, VariableInstance> variableInstances = taskService.getVariableInstances("80006");
        System.err.println("二级管理员把任务打回商户");
        System.err.println(variableInstances);
        taskService
                .complete("80006", map);
    }/*
    二级管理员同意审批
{approval=VariableInstanceEntity[id=60002, name=approval, type=serializable, byteArrayValueId=60001]}
    */
}
