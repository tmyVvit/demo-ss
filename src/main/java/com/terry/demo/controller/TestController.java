package com.terry.demo.controller;

import com.terry.demo.model.ListInfoModel;
import com.terry.demo.model.ListMetaModel;
import com.terry.demo.service.ListManagementService;
import com.terry.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private ListManagementService listManagementService;

    @GetMapping("/test")
    public String test() {
        testService.test();
        return "test";
    }

    @PostMapping("/list-info/insert")
    public int insert(@RequestBody ListInfoModel model) {
        return listManagementService.insertListInfo(model);
    }

    @GetMapping("/list-info/select")
    public ListInfoModel select(@RequestParam(required = false) Long id, @RequestParam String createTime) {
        return listManagementService.selectListInfo(id, LocalDateTime.parse(createTime));
    }

    @GetMapping("/list-meta/select")
    public ListMetaModel listMetaModel(@RequestParam Long id) {
        return listManagementService.selectListMeta(id);
    }
}
