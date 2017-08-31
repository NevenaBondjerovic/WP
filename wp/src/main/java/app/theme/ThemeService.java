package app.theme;

import java.util.List;

public interface ThemeService {

	List<Theme> findAll();
	Theme findOne(String id);
	List<Theme> findByForum(String forumName);
	Theme save(Theme theme);
	Theme update(Theme theme);
	Boolean delete(String id);
}
