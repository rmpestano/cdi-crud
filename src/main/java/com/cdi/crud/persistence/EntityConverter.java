package com.cdi.crud.persistence;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.cdi.crud.model.BaseEntity;

@FacesConverter(forClass=BaseEntity.class)
public class EntityConverter implements Converter {

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object entity) {
		if (entity == null || "".equals(entity)) {
			return null;
		}
		
		if (!getEntityMap(context).containsKey(entity)) {
			String uuid = UUID.randomUUID().toString();
			getEntityMap(context).put(entity, uuid);
			return uuid;
		} else {
			return getEntityMap(context).get(entity);
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String uuid) {
		Set<Entry<Object, String>> entries = getEntityMap(context).entrySet();
		for (Entry<Object, String> entry : entries) {
			if (entry.getValue().equals(uuid)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Object, String> getEntityMap(FacesContext context) {
		Map<String, Object> viewMap = context.getViewRoot().getViewMap();
		Map<Object, String> entities = (Map<Object, String>) viewMap.get("entities");
		if (entities == null) {
			entities = new HashMap<>();
			viewMap.put("entities", entities);
		}
		return entities;
	}
}
