package ru.curs.lyra.controller;


import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.curs.lyra.dto.DataParams;
import ru.curs.lyra.dto.MetaDataParams;
import ru.curs.lyra.kernel.LyraCallContext;
import ru.curs.lyra.service.LyraService;

import java.util.Map;

@RestController
@RequestMapping("/lyra")
@SuppressWarnings("unused")
public class LyraController {
    private final LyraService srv;

    public LyraController(LyraService srv) {
        this.srv = srv;
    }


    @PostMapping("/metadata")
    public String getMetadata(@RequestParam Map<String, String> body) throws Exception {

        MetaDataParams params = new MetaDataParams();
        params.setContext(body.get("context"));
        params.setFormClass(body.get("formClass"));
        params.setInstanceId(body.get("instanceId"));

        LyraCallContext ctx = new LyraCallContext();
        ctx.setLyraContext(new JSONObject(params.getContext()));

        return srv.getMetadata(ctx, params);
    }


    @PostMapping("/data")
    public ResponseEntity getData(@RequestParam Map<String, String> body) throws Exception {

        DataParams params = new DataParams();
        params.setContext(body.get("context"));
        params.setOffset(Integer.parseInt(body.get("offset")));
        params.setLimit(Integer.parseInt(body.get("limit")));
        params.setDgridOldPosition(Integer.parseInt(body.get("dgridOldPosition")));
        params.setSortingOrFilteringChanged(Boolean.parseBoolean(body.get("sortingOrFilteringChanged")));
        params.setFirstLoading(Boolean.parseBoolean(body.get("firstLoading")));
        params.setRefreshId(body.get("refreshId"));
        params.setFormClass(body.get("formClass"));
        params.setInstanceId(body.get("instanceId"));

        LyraCallContext ctx = new LyraCallContext();
        ctx.setLyraContext(new JSONObject(params.getContext()));

        String data = srv.getData(ctx, params);


        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        int totalCount = params.getTotalCount();
        int firstIndex = params.getOffset();
        int lastIndex = params.getOffset() + params.getLimit() - 1;
        responseHeaders.set("Content-Range", "items " + firstIndex + "-"
                + lastIndex + "/" + totalCount);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(data, responseHeaders, HttpStatus.OK);

        return responseEntity;

    }


}
