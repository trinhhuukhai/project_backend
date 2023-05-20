package com.project.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {
  private String errorCode;
  private String message;
  private Object data;
  private int count; // add a count field

  public ResponseResult(ResponseCode responseCode) {
    this.errorCode = responseCode.getErrorCode();
    this.message = responseCode.getMessage();
  }

  public static ResponseResult success(Object data) {
    ResponseResult resp = new ResponseResult();
    resp.errorCode = ResponseCode.SUCCESS.getErrorCode();
    resp.message = ResponseCode.SUCCESS.getMessage();
    resp.setData(data);
    resp.setCount(data instanceof Collection ? ((Collection<?>) data).size() : 1); // set the count based on the data object
    return resp;
  }

  // getters and setters for count

}
