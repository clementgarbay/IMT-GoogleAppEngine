package com.zenika.zencontact.resource;

import com.googlecode.objectify.ObjectifyFilter;

import javax.servlet.annotation.WebFilter;

/**
 * @author Clément Garbay
 */
@WebFilter(urlPatterns = {"/*"})
public class ObjectifyWebFilter extends ObjectifyFilter {}
