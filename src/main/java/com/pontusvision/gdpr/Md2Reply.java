package com.pontusvision.gdpr;

import java.io.Serializable;

public class Md2Reply implements  Serializable{
  public class Register implements Serializable {
    Integer request;
    String name;
    String server;
    String fileType;
    String path;
    String sizeBytes;
    String owner;
    String created;

    public Integer getRequest() {
      return request;
    }

    public void setRequest(Integer request) {
      this.request = request;
    }

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

    public String getSizeBytes() {
      return sizeBytes;
    }

    public void setSizeBytes(String sizeBytes) {
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

    String lastAccess;
  }

  Integer total;
  Register track;

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Register getTrack() {
    return track;
  }

  public void setTrack(Register track) {
    this.track = track;
  }


}
