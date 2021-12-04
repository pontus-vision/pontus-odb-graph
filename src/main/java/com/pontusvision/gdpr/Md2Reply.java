package com.pontusvision.gdpr;

import java.io.Serializable;

public class Md2Reply extends BaseReply {


  Long total;
  Long reqId;
  Register[] track;

  public Md2Reply() {
    this(Status.OK);
  }


  public Md2Reply(Status status) {
    super(status);
  }
  public Md2Reply(Status status, String err) {
    super(status);
    this.setErrorStr(err);
  }
  public Long getReqId() {
    return reqId;
  }

  public void setReqId(Long reqId) {
    this.reqId = reqId;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Register[] getTrack() {
    return track;
  }

  public void setTrack(Register[] track) {
    this.track = track;
  }

  public static class Register implements Serializable {
    String name;
    String server;
    String fileType;
    String path;
    Long sizeBytes;
    String owner;
    String created;
    String lastAccess;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getServer() {
      return server;
    }

    public void setServer(String server) {
      this.server = server;
    }

    public String getFileType() {
      return fileType;
    }

    public void setFileType(String fileType) {
      this.fileType = fileType;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public Long getSizeBytes() {
      return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
      this.sizeBytes = sizeBytes;
    }

    public String getOwner() {
      return owner;
    }

    public void setOwner(String owner) {
      this.owner = owner;
    }

    public String getCreated() {
      return created;
    }

    public void setCreated(String created) {
      this.created = created;
    }

    public String getLastAccess() {
      return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
      this.lastAccess = lastAccess;
    }
  }


}
