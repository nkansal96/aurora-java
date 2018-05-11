package com.auroraapi.models;

/** Aurora Exception is the Exception thrown for API calls.
 */
public class AuroraException extends Exception {

    private String id;
    private String type;
    private String code;
    private int status;
    private String message;

    public AuroraException(String message, String id, String type, String code, int status) {
        super();
        this.message = message;
        this.id = id;
        this.type = type;
        this.code = code;
        this.status = status;
    }

    /**
     * @return A unique ID assigned to this request
     */
    public String getId() {
        return id;
    }

    /**
     * @return A textual representation of the HTTP status (e.g. BadRequest, InternalServerError)
     */
    public String getType() {
        return type;
    }

    /**
     * @return A brief, textual representation of what went wrong
     */
    public String getCode() {
        return code;
    }

    /**
     * @return The HTTP status code (e.g. 401, 500, etc.)
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return A longer description of what happened and possibly steps to resolve the error
     */
    @Override
    public String getMessage() {
        return message;
    }
}
