package com.pontusvision.gdpr;

import javax.ws.rs.core.*;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Md2Reply extends Response implements  Serializable   {


  public static class Register implements Serializable {
    String name;
    String server;
    String fileType;
    String path;
    Long sizeBytes;
    String owner;
    String created;


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

    String lastAccess;
  }

  Long total;
  Long reqId;

  Register[] track;

  Status status = Status.OK;
  Response localResp;
  public Md2Reply() {
    this(Status.OK);
  }

  public Md2Reply(Status status) {
    this.status = status;
    localResp = Response.status(this.status).entity(this).build();

  }

  @Override
  public int getStatus() {
    return status.getStatusCode();
  }

  @Override
  public StatusType getStatusInfo() {
    return status;
  }

  @Override
  public Object getEntity() {
    return localResp.getEntity();
  }

  @Override
  public <T> T readEntity(Class<T> entityType) {
    return localResp.readEntity(entityType);
  }

  @Override
  public <T> T readEntity(GenericType<T> entityType) {
    return localResp.readEntity(entityType);
  }

  @Override
  public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
    return localResp.readEntity(entityType,annotations);
  }

  @Override
  public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
    return localResp.readEntity(entityType,annotations);
  }

  @Override
  public boolean hasEntity() {
    return localResp.hasEntity();
  }

  @Override
  public boolean bufferEntity() {
    return localResp.bufferEntity();
  }

  @Override
  public void close() {
    localResp.close();
  }

  @Override
  public MediaType getMediaType() {
    return localResp.getMediaType();
  }

  @Override
  public Locale getLanguage() {
    return localResp.getLanguage();
  }

  @Override
  public int getLength() {
    return localResp.getLength();
  }

  @Override
  public Set<String> getAllowedMethods() {
    return localResp.getAllowedMethods();
  }

  @Override
  public Map<String, NewCookie> getCookies() {
    return localResp.getCookies();
  }

  @Override
  public EntityTag getEntityTag() {
    return localResp.getEntityTag();
  }

  @Override
  public Date getDate() {
    return localResp.getDate();
  }

  @Override
  public Date getLastModified() {
    return localResp.getLastModified();
  }

  @Override
  public URI getLocation() {
    return localResp.getLocation();
  }

  @Override
  public Set<Link> getLinks() {
    return localResp.getLinks();
  }

  @Override
  public boolean hasLink(String relation) {
    return localResp.hasLink(relation);
  }

  @Override
  public Link getLink(String relation) {
    return localResp.getLink(relation);
  }

  @Override
  public Link.Builder getLinkBuilder(String relation) {
    return localResp.getLinkBuilder(relation);
  }

  @Override
  public MultivaluedMap<String, Object> getMetadata() {
    return localResp.getMetadata();
  }

  @Override
  public MultivaluedMap<String, String> getStringHeaders() {
    return localResp.getStringHeaders();
  }

  @Override
  public String getHeaderString(String name) {
    return localResp.getHeaderString(name);
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


}
