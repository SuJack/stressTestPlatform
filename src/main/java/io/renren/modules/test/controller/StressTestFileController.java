package io.renren.modules.test.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.test.entity.StressTestFileEntity;
import io.renren.modules.test.jmeter.JmeterStatEntity;
import io.renren.modules.test.service.StressTestFileService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 压力测试用例文件
 *
 */
@RestController
@RequestMapping("/test/stressFile")
public class StressTestFileController {
    @Autowired
    private StressTestFileService stressTestFileService;

    /**
     * 参数化文件，用例文件列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("test:stress:fileList")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        List<StressTestFileEntity> jobList = stressTestFileService.queryList(query);
        int total = stressTestFileService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(jobList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询具体文件信息
     */
    @RequestMapping("/info/{fileId}")
    public R info(@PathVariable("fileId") Long fileId){
        StressTestFileEntity stressTestFile = stressTestFileService.queryObject(fileId);
        return R.ok().put("stressTestFile", stressTestFile);
    }

    /**
     * 修改性能测试用例脚本文件
     */
    @SysLog("修改性能测试用例")
    @RequestMapping("/update")
    @RequiresPermissions("test:stress:fileUpdate")
    public R update(@RequestBody StressTestFileEntity stressTestFile) {
        ValidatorUtils.validateEntity(stressTestFile);

        stressTestFileService.update(stressTestFile);

        return R.ok();
    }

    /**
     * 删除指定文件
     */
    @SysLog("删除性能测试用例文件")
    @RequestMapping("/delete")
    @RequiresPermissions("test:stress:fileDelete")
    public R delete(@RequestBody Long[] fileIds) {
        stressTestFileService.deleteBatch(fileIds);

        return R.ok();
    }

    /**
     * 立即执行性能测试用例，当前仅支持同一时间执行一个性能测试用例。
     */
    @SysLog("立即执行性能测试用例脚本文件")
    @RequestMapping("/runOnce")
    @RequiresPermissions("test:stress:runOnce")
    public R run(@RequestBody Long[] fileIds) {

        stressTestFileService.run(fileIds);
        return R.ok();
    }

    /**
     * 停止性能测试用例
     */
    @SysLog("停止执行性能测试用例脚本")
    @RequestMapping("/stopAll")
    @RequiresPermissions("test:stress:stopAll")
    public R stopAll() {
        stressTestFileService.stopAll();

        return R.ok();
    }

    /**
     * 立即停止性能测试用例，如果是脚本方式运行希望是杀掉进程（节点机+主节点）。
     */
    @SysLog("立即停止性能测试用例脚本")
    @RequestMapping("/stopAllNow")
    @RequiresPermissions("test:stress:stopAllNow")
    public R stopAllNow() {
        stressTestFileService.stopAllNow();

        return R.ok();
    }

    /**
     * 定时查询执行结果。
     * 只有在本地执行性能测试时，才会被调用。
     * 不要求权限校验了，频繁操作不用每次都调用数据库。
     */
    @RequestMapping("/statInfo/{fileId}")
    public R statInfo(@PathVariable("fileId") Long fileId){
        // 频率不是特别高，controller层仍然是new一个对象。
        JmeterStatEntity jmeterStatEntity = new JmeterStatEntity(fileId);
        return R.ok().put("statInfo", jmeterStatEntity);
    }
}