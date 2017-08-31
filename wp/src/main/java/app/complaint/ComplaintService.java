package app.complaint;

import java.util.List;

public interface ComplaintService {

	List<Complaint> findAll();
	Complaint findOne(Integer id);
	Complaint save(Complaint complaint);
	Complaint update(Complaint complaint);
	Integer findId();
	Boolean delete(Integer id);
	
}
