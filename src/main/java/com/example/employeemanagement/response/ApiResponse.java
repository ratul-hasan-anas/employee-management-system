
package com.example.employeemanagement.response;

// Standard API response structure
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Constructor
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Success response
    public static <T> ApiResponse<T> success(
            String message, T data) {

        return new ApiResponse<>(true, message, data);
    }

    // Error response
    public static <T> ApiResponse<T> failure(
            String message, T data) {

        return new ApiResponse<>(false, message, data);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
