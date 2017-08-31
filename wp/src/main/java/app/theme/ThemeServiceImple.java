package app.theme;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.comment.Comment;
import app.comment.CommentRepository;
import app.complaint.Complaint;
import app.complaint.ComplaintRepository;
import app.user.User;
import app.user.UserRepository;

@Service
@Transactional
public class ThemeServiceImple implements ThemeService{

	private final ThemeRepository themeRepo;
	private final CommentRepository commRepo;
	private final ComplaintRepository complaintRepo;
	private final UserRepository userRepo;

	@Autowired
	public ThemeServiceImple(final ThemeRepository themeRepo,final CommentRepository commRepo,
			final ComplaintRepository complaintRepo,final UserRepository userRepo) {
		this.themeRepo = themeRepo;
		this.commRepo = commRepo;
		this.complaintRepo=complaintRepo;
		this.userRepo=userRepo;
	}

	@Override
	public List<Theme> findAll() {
		return themeRepo.findAll();
	}

	@Override
	public Theme findOne(String id) {
		return themeRepo.findOne(id);
	}

	@Override
	public List<Theme> findByForum(String forumName) {
		List<Theme> themes = themeRepo.findAll();
		List<Theme> returnList = new ArrayList<Theme>();
		for (Theme theme : themes) {
			if(theme.getForum()!=null && theme.getForum().getName().equals(forumName))
				returnList.add(theme);
				
		}
		return returnList;
	}

	@Override
	public Theme save(Theme theme) {
		if(themeRepo.findOne(theme.getId())!=null)	
			return	null;
		return themeRepo.save(theme);
	}

	@Override
	public Theme update(Theme theme) {
		return themeRepo.update(theme);
	}

	@Override
	public Boolean delete(String id) {
		Theme theme=themeRepo.findOne(id);
		if(theme!=null){
			if(themeRepo.delete(id)){
				List<Complaint> complaintToDelete = new ArrayList<Complaint>();
				for(Comment com : theme.getComments()){
					com=commRepo.findOne(com.getId());
					for(Comment subC : com.getSubComments()){
						deleteFromUserComments(subC.getId());
					}
					commRepo.delete(com.getId());
					for(Complaint complaint : complaintRepo.findAll()){
						if(complaint.getComment()!=null 
								&& complaint.getComment().getId().equals(com.getId())){
							complaintToDelete.add(complaint);
						}
					}
					deleteFromUserComments(com.getId());
				}
				
				List<String> inactiveToDelete = new ArrayList<String>();
				for(Comment com: commRepo.getComments()){
					if(!com.getActive() && com.getTheme().getId().equals(theme.getId())){
						com=commRepo.findOneWithInactive(com.getId());
						inactiveToDelete.add(com.getId());
						for(Comment subC:com.getSubComments()){
							deleteFromUserComments(subC.getId());
						}
					}
				}
				for(String inactiveId : inactiveToDelete){
					commRepo.delete(inactiveId);
					for(Complaint complaint : complaintRepo.findAll()){
						if(complaint.getComment()!=null 
								&& complaint.getComment().getId().equals(inactiveId)){
							complaintToDelete.add(complaint);
						}
					}
					deleteFromUserComments(inactiveId);
				}
				
				for(Complaint complaint : complaintRepo.findAll()){
					if(complaint.getTheme()!=null 
							&& complaint.getTheme().getId().equals(theme.getId())){
						complaintToDelete.add(complaint);
					}
				}
				
				for(Complaint cDelete:complaintToDelete){
					deleteComplaint(cDelete.getId());
				}
				
				deleteFromUserThemes(theme.getId());
				
				return true;
			}
		}
		return false;
	}
	
	private void deleteComplaint(Integer id){
		if(complaintRepo.delete(id)){
			for(User u : userRepo.findAll()){
				for(int i=0;i<u.getComplaints().size();i++){
					if(u.getComplaints().get(i).getId().equals(id)){
						u.getComplaints().remove(i);
						userRepo.update(u);
						break;
					}
				}
			}
		}
	}
	
	private void deleteFromUserThemes(String id){
		for(User u:userRepo.findAll()){
			for(int i=0;i<u.getThemes().size();i++){
				if(u.getThemes().get(i).getId().equals(id)){
					u.getThemes().remove(i);
					userRepo.update(u);
					break;
				}
			}
		}
	}
	
	private void deleteFromUserComments(String id){
		for(User u:userRepo.findAll()){
			for(int i=0;i<u.getComments().size();i++){
				if(u.getComments().get(i).getId().equals(id)){
					u.getComments().remove(i);
					userRepo.update(u);
					break;
				}
			}
		}
	}
	
}
