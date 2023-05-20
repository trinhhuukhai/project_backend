package com.project.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum  ResponseCode {

  ERROR("ERROR", "Lỗi không xác định"),
  ERR_INPUT("ERR_INPUT", "Thiếu dữ liệu đầu vào"),
  ERROR_USER_EXIST("ERROR_USER_EXIST", "Tài khoản đã tồn tại"),
  ACCESS_DENIED("ACCESS_DENIED", "Truy cập bị từ chối"),
  SUCCESS("200", "Thành công"),
  ERROR_AUTH("ERROR_AUTH", "Tên tài khoản hoặc mật khẩu không chính xác");

  String errorCode;
  String message;

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
