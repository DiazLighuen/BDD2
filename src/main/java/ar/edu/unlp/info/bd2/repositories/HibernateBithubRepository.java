package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.*;

import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class HibernateBithubRepository implements BithubRepository<Long>{

    @Autowired
    private SessionFactory sessionFactory;

    public HibernateBithubRepository (){

    }

    public Object getResultList (String string) {
        return sessionFactory.getCurrentSession().createQuery(string).getResultList();
    }

    public Object save (Object object) {
        sessionFactory.getCurrentSession().save(object);
        return object;
    }

    public User saveUser (User user) {
        this.save(user);
        return user;
    }

    public Tag saveTag (Tag tag) {
        this.save(tag);
        return tag;
    }

    public Branch saveBranch (Branch branch) {
        this.save(branch);
        return branch;
    }

    public File saveFile (File file) {
        this.save(file);
        return file;
    }

    public Commit saveCommit (Commit commit){
        this.save(commit);
        return commit;
    }

    public Review saveReview (Review review){
        this.save(review);
        return review;
    }

    public FileReview saveFileReview (FileReview fileReview){
        this.save(fileReview);
        return fileReview;
    }

    public Boolean existNameBranch (String name) {
        String hql = "from Branch b where b.name = '"+name+"'";
        List<Branch> result = (List<Branch>) this.getResultList(hql);
        if (!result.isEmpty()){
            return true;
        }
        return false;
    }

    public Boolean existEmailUser (String email) {
        String hql = "from User u where u.email = '"+email+"'";
        List<User> result = (List<User>) this.getResultList(hql);
        if (!result.isEmpty()){
            return true;
        }
        return false;
    }

    public Commit getCommitByHash (String hash) {
        String hql = "from Commit c where c.hash = '"+hash+"'";
        List<Commit> result = (List<Commit>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public Branch getBranchByName (String name) {
        String hql = "from Branch b where b.name = '"+name+"'";
        List<Branch> result = (List<Branch>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public Optional<Tag> getTagByName(String tagName) {
        String hql = "from Tag t where t.name = '"+tagName+"'";
        List<Tag> result = (List<Tag>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return Optional.ofNullable(result.get(0));
        }
        return Optional.ofNullable(null);
    }

    public Optional<Review> getReviewById(Long id) {
        String hql = "from Review r where r.id = '"+id+"'";
        List<Review> result = (List<Review>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return Optional.ofNullable(result.get(0));
        }
        return null;
    }

    public List<Commit> getAllCommitsForUser(Long userId) {
        String hql = "from Commit c where c.author = '"+userId+"'";
        List<Commit> result = (List<Commit>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return result;
        }
        return null;
    }

    public Map<Long, Long> getCommitCountByUser() {
        String hql = "from User u";
        List<User> result = (List<User>) this.getResultList(hql);
        Map<Long, Long> map = new HashMap<Long, Long>();
        for (User i : result) map.put(Long.valueOf(i.getId()),Long.valueOf(this.getAllCommitsForUser(i.getId()).size()));
        return map;
    }

    private User getUserById(Long id){
        String hql = "from User u where u.id = '"+id+"'" ;
        List<User> result = (List<User>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public List<User> getUsersThatCommittedInBranch(String branchName) {
        Branch branch = this.getBranchByName(branchName);
        String hql = "select distinct c.author from Commit c where c.branch = '"+branch.getId()+"'";
        List<User> users = (List<User>) this.getResultList(hql);
        return users;
    }

    @Override
    public User getUserByEmail(String email) {
        String hql = "from User u where u.email = '"+email+"'";
        List<User> result = (List<User>) this.getResultList(hql);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}