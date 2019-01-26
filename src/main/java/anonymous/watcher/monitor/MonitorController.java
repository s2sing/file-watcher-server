package anonymous.watcher.monitor;

import anonymous.watcher.filesystem.WatcherSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MonitorController {

    @Autowired
    private WatcherSessionRepository watcherSessionRepository;

    @GetMapping("/monitor")
    public String monitor(Model model) {
        model.addAttribute("users", watcherSessionRepository.getAll());
        return "monitor";
    }

}
