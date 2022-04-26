package com.g.model.support;


import com.g.model.KeyEntity;
import com.g.model.SharkEntity;
import com.g.model.UserEntity;
import com.mezingr.dao.HibernateDAO;
import com.mezingr.dao.HibernateDAOFactory;
import com.mezingr.hibernate.HibernateTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class XDAOSupport {
	
	private HibernateDAOFactory hibernateDAOFactory = null;
	private HibernateTemplateFactory templateFactory = null;
	
	public HibernateTemplateFactory getTemplateFactory() {
		return templateFactory;
	}
	
	@Autowired
	public void setTemplateFactory(HibernateTemplateFactory templateFactory) {
		this.templateFactory = templateFactory;
	}
	
	@Autowired
	public void setFactory(HibernateDAOFactory hibernateDAOFactory) {
		this.hibernateDAOFactory = hibernateDAOFactory;
	}
	//角色相关
	public HibernateDAO<UserEntity> getUserEntityDAO() {
		return this.hibernateDAOFactory.getHibernateDAO(UserEntity.class);
	}
	
	public HibernateDAO<KeyEntity> getKeyEntityDAO() {
		return this.hibernateDAOFactory.getHibernateDAO(KeyEntity.class);
	}

	public HibernateDAO<SharkEntity> getSharkEntityDAO() {
		return this.hibernateDAOFactory.getHibernateDAO(SharkEntity.class);
	}
	
}
