package org.example.server.manager;

import org.example.common.models.Organization;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private PriorityQueue<Organization> collection = new PriorityQueue<Organization>();
    private DatabaseManager databaseManager;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public CollectionManager(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
        loadFromDatabase();
    }

    private void loadFromDatabase(){
        lock.writeLock().lock();
        try {
            List<Organization> organizations = databaseManager.getAllOrganizations();
            collection.addAll(organizations);
            System.out.println("Загружено " + collection.size() + " организаций");
        } catch (SQLException e){
            System.out.println("Произошла ошибка загрузки из БД: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Organization getById(long id){
        lock.readLock().lock();
        try {
            for (Organization element: collection){
                if(element.getId() == id){
                    return element;
                }
            } return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean add (Organization organization, Long ownerId){
        lock.writeLock().lock();
        try {
            long newId = databaseManager.saveOrganization(organization, ownerId);
            organization.setId(newId);
            organization.setOwnerId(ownerId);
            collection.add(organization);
            return true;
        } catch (SQLException e) {
            System.out.println("Произошла ошибка добавления организации в БД: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeById(long id){
        lock.writeLock().lock();
        try {
            Organization organization = getById(id);
            if (organization == null){
                return false;
            }
            boolean removed = collection.remove(organization);
            if (removed) {
                return databaseManager.deleteOrganization(id);
            }
            return false;
        } catch (SQLException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean update(Organization updatedOrg){
        lock.writeLock().lock();
        try {
            Organization oldOrg = getById(updatedOrg.getId());
            if (oldOrg == null) return false;

            collection.remove(oldOrg);
            boolean success = databaseManager.updateOrganization(updatedOrg);

            if (success){
                collection.add(updatedOrg);
            }
            return success;
        } catch (SQLException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(){
        lock.writeLock().lock();
        try {
            databaseManager.clearAllOrganizations();
            collection.clear();
            System.out.println("Коллекция очищена");
        } catch (SQLException e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }


    public int size() {
        lock.readLock().lock();
        try {
            return collection.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return collection.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Organization> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(collection);
        } finally {
            lock.readLock().unlock();
        }
    }


    public PriorityQueue<Organization> getCollection() {
        lock.readLock().lock();
        try {
            return collection;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String sort() {
        lock.readLock().lock();
        try{
            if (collection.isEmpty()) {
                return "Коллекция пуста";
            }
            return collection.stream()
                    .sorted(Comparator.comparingLong(Organization::getId))
                    .map(Organization::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.readLock().unlock();
        }
    }

    public String sortFieldDescendingType() {
        lock.readLock().lock();
        try {
            if (collection.isEmpty()) {
                return "Коллекция пуста";
            }
            return collection.stream()
                    .sorted(Comparator.comparingLong(Organization::getId).reversed())
                    .map(org -> org.getType().toString())
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.readLock().unlock();
        }
    }

    public String sortDescending() {
        lock.readLock().lock();
        try {
            if (collection.isEmpty()) {
                return "Коллекция пуста";
            }
            return collection.stream()
                    .sorted(Comparator.comparingLong(Organization::getId).reversed())
                    .map(Organization::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isOwner(long orgId, long userId) {
        lock.readLock().lock();
        try {
            Organization org = getById(orgId);
            return org != null && org.getOwnerId() != null && org.getOwnerId().equals(userId);
        } finally {
            lock.readLock().unlock();
        }
    }

}

