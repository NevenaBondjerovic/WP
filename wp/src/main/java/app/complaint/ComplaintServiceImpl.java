package app.complaint;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.user.User;
import app.user.UserRepository;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService{

	private final ComplaintRepository complaintRepo;
	private final UserRepository userRepo;

	@Autowired	
	public ComplaintServiceImpl(final ComplaintRepository complaintRepo,
			final UserRepository userRepo) {
		this.complaintRepo = complaintRepo;
		this.userRepo=userRepo;
	}

	@Override
	public List<Complaint> findAll() {
		return complaintRepo.findAll();
	}

	@Override
	public Complaint findOne(Integer id) {
		return complaintRepo.findOne(id);
	}

	@Override
	public Complaint save(Complaint complaint) {
		if(complaintRepo.findOne(complaint.getId())!=null)
			return null;
		return complaintRepo.save(complaint);
	}

	@Override
	public Complaint update(Complaint complaint) {
		return complaintRepo.update(complaint);
	}

	@Override
	public Integer findId() {
		return complaintRepo.findId();
	}

	@Override
	public Boolean delete(Integer id) {
		if(complaintRepo.delete(id)==false){
			return false;
		}else{
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
		return true;
	}

}
