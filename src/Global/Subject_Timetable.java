package Global;

import DB.*;
import DB.Teacher;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.jws.WebService;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anil on 26/03/2018
 */
@Path("/subject_time_services")
@WebService
public class Subject_Timetable {

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("add")
    public String add(@FormParam("param1")int slot, @FormParam("param2") int day, @FormParam("param3")String sid, @FormParam("param4") String did, @FormParam("param5")int clid, @FormParam("param6")String t1, @FormParam("param7")String t2)
    {
        Session session = Global.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Division division= (Division) session.createQuery("from Division d where d.name=:id and d.csClass.id=:id1").setParameter("id",did).setParameter("id1", clid).uniqueResult();
            Subject subject= (Subject) session.createQuery("from Subject s where s.id=:id").setParameter("id",sid).uniqueResult();
            SubjectTimetable subjectTimetable=new SubjectTimetable();
            subjectTimetable.setSlotno(slot);
            subjectTimetable.setDay(day);
            subjectTimetable.setDivision(division);
            subjectTimetable.setSubject(subject);

            LocalTime l1=LocalTime.parse(t1);
            LocalTime l2=LocalTime.parse(t2);
            subjectTimetable.setStime(l1);
            subjectTimetable.setEtime(l2);


            session.persist(subjectTimetable);
            transaction.commit();
            session.close();
            return "1";
        }
        catch (Exception e)
        {
            transaction.commit();
            session.close();
            return "E";
        }
    }
}
