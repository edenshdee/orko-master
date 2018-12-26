package com.gruelbox.orko.job.script;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.gruelbox.orko.OrkoConfiguration;
import com.gruelbox.orko.auth.Hasher;
import com.gruelbox.orko.auth.Roles;
import com.gruelbox.orko.jobrun.JobResource;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Submission and management of scripts and script jobs.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class ScriptResource implements WebResource {

  private final JobResource jobResource;
  private final Hasher hasher;
  private final OrkoConfiguration configuration;
  private final ScriptAccess scriptAccess;

  @Inject
  ScriptResource(JobResource jobResource, ScriptAccess scriptAccess, OrkoConfiguration configuration, Hasher hasher) {
    this.jobResource = jobResource;
    this.scriptAccess = scriptAccess;
    this.configuration = configuration;
    this.hasher = hasher;
  }

  @PUT
  @Timed
  @Path("/scripts/{id}")
  @UnitOfWork
  @RolesAllowed(Roles.TRADER)
  public Response putScript(@PathParam("id") String id, Script script) throws AuthenticationException {
    if (!id.equals(script.id()))
      return Response.status(400).entity(ImmutableMap.of("error", "id doesn't match endpoint")).build();
    scriptAccess.insert(script);
    return Response.ok().build();
  }

  @POST
  @Timed
  @Path("/scripts/{id}")
  @UnitOfWork
  @RolesAllowed(Roles.TRADER)
  public Response updateScript(@PathParam("id") String id, Script script) throws AuthenticationException {
    if (!id.equals(script.id()))
      return Response.status(400).entity(ImmutableMap.of("error", "id doesn't match endpoint")).build();
    scriptAccess.update(script);
    return Response.ok().build();
  }

  @DELETE
  @Timed
  @Path("/scripts/{id}")
  @UnitOfWork
  @RolesAllowed(Roles.TRADER)
  public void deleteScript(@PathParam("id") String id) throws AuthenticationException {
    scriptAccess.delete(id);
  }

  @GET
  @Timed
  @Path("/scripts")
  @UnitOfWork(readOnly = true)
  @RolesAllowed(Roles.TRADER)
  public List<Script> listScripts() throws AuthenticationException {
    return scriptAccess.list();
  }

  @PUT
  @Timed
  @Path("/jobs/scripts/{id}")
  @UnitOfWork
  @RolesAllowed(Roles.TRADER)
  public Response putJob(@PathParam("id") String id, ScriptJobPrototype job) throws AuthenticationException {
    return jobResource.put(id, ScriptJob.builder()
        .id(job.id)
        .name(job.name)
        .script(job.script)
        .scriptHash(hasher.hashWithString(job.script, configuration.getScriptSigningKey()))
        .build());
  }

  public static final class ScriptJobPrototype {
    public String id;
    public String name;
    public String script;
  }
}