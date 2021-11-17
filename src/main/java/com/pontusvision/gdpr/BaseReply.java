package com.pontusvision.gdpr;

import javax.ws.rs.core.*;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BaseReply extends Response implements  Serializable   {


  String errorStr = null;

  Status status = Status.OK;
  Response localResp;
  public BaseReply() {
    this(Status.OK);
  }

  public BaseReply(Status status) {
    this.status = status;
    localResp = Response.status(this.status).entity(this).build();

  }

  public String getErrorStr() {
    return errorStr;
  }

  public void setErrorStr(String errorStr) {
    this.errorStr = errorStr;
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


}
