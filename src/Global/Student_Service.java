package Global;

import DB.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Anil on 29/01/2018
 */
@Path("/student_services")
@WebService
public class Student_Service {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("add")
    public String add(@FormParam("param1")String studjson,@FormParam("param2") int cl, @FormParam("param3") String dv)
    {
        Session session= Global.getSession();
        Transaction transaction=session.beginTransaction();
        try
        {
            Object object=null;
            JSONArray arrayObj=null;
            JSONParser jsonParser=new JSONParser();
            object=jsonParser.parse(studjson);
            arrayObj=(JSONArray) object;

            Division division= (Division) session.createQuery("from Division d where d.name=:id and d.csClass.id=:id1").setParameter("id",dv).setParameter("id1", cl).uniqueResult();

            for (int i = 0; i < arrayObj.size(); i++)
            {
                JSONObject jsonObj = (JSONObject) arrayObj.get(i);
                if(jsonObj!=null)
                {
                    Student s=new Student();
                    String nme= (String) jsonObj.get("roll");
                    s.setRoll(Integer.parseInt(nme));
                    s.setDivision(division);
                    s.setName((String) jsonObj.get("name"));
                    session.persist(s);
                }
            }
            transaction.commit();
            session.close();
            return "1";
        }
        catch (Exception e)
        {
            transaction.commit();
            session.close();
            return ""+e+"";
        }
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("viewDivisionWise")
    public List viewDivisionWise(@FormParam("param1")String div,@FormParam("param2")int clid)
    {
        Session session1= Global.getSession();
        Transaction t=session1.beginTransaction();
        try
        {

            java.util.List<Student> tlist=session1.createQuery("from Student s where s.division.name=:id and s.division.csClass.id=:id1").setParameter("id1",clid).setParameter("id",div).list();
            List list=new ArrayList();
            for(Iterator iterator = tlist.iterator(); iterator.hasNext();)
            {
                Student student= (Student) iterator.next();
                List list1=new ArrayList();
                list1.add(student.getRoll());
                list1.add(student.getName());
                list1.add(student.getRoll());
                list.add(list1);
            }
            t.commit();
            session1.close();
            return list;
        }
        catch (Exception e)
        {
//            t.commit();
            session1.close();
            return null;
        }
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("viewLabBatchWise")
    public List viewLabBatchWise(@FormParam("param1")String lname,@FormParam("param2")int clid)
    {
        Session session1= Global.getSession();
        Transaction t=session1.beginTransaction();
        try
        {
            LabBatch labBatch= (LabBatch) session1.createQuery("from LabBatch s where s.name=:id and s.csClass.id=:id1").setParameter("id",lname).setParameter("id1",clid).uniqueResult();
            java.util.List<Student> tlist=session1.createQuery("from Student s where s.division.csClass.id=:id and s.roll between :id1 and :id2").setParameter("id1",labBatch.getFrom()).setParameter("id2",labBatch.getTo()).setParameter("id",clid).list();
            List list=new ArrayList();
            for(Iterator iterator = tlist.iterator(); iterator.hasNext();)
            {
                Student student= (Student) iterator.next();
                List list1=new ArrayList();
                list1.add(student.getRoll());
                list1.add(student.getName());
                list1.add(student.getRoll());
                list1.add(student.getDivision().getName());
                list.add(list1);
            }
            t.commit();
            session1.close();
            return list;
        }
        catch (Exception e)
        {
//            t.commit();
            session1.close();
//            return Collections.singletonList("" + e + "");
            return null;
        }
    }
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("delete")
    public String delete(@FormParam("param1") int rol,@FormParam("param2")int clid, @FormParam("param3")String dname)
    {
        Session session= Global.getSession();
        Transaction t=session.beginTransaction();
        DB.Student student= (Student) session.createQuery("from Student s where s.roll=:id and s.division.name=:id1 and s.division.csClass.id=:id2").setParameter("id",rol).setParameter("id1",dname).setParameter("id2",clid).uniqueResult();
        if (student==null) {

            return "0";
        }
        else
        {
//            Query query=session.createQuery("delete from SubjectAttendance s where s.student.roll=:id and s.student.division.name=:id1 and s.student.division.csClass.id=:id2").setParameter("id",rol).setParameter("id1",dname).setParameter("id2",clid);
//            Query query1=session.createQuery("delete from LabAttendance s where s.student.roll=:id and s.student.division.name=:id1 and s.student.division.csClass.id=:id2").setParameter("id",rol).setParameter("id1",dname).setParameter("id2",clid);
//            query.executeUpdate();
//            query1.executeUpdate();

            List<SubjectAttendance> slist=session.createQuery("from SubjectAttendance s where s.student.roll=:id and s.student.division.name=:id1 and s.student.division.csClass.id=:id2").setParameter("id",rol).setParameter("id1",dname).setParameter("id2",clid).list();
            for(Iterator iterator = slist.iterator(); iterator.hasNext();)
            {
                SubjectAttendance subjectAttendance= (SubjectAttendance) iterator.next();
                session.delete(subjectAttendance);
            }
            List<LabAttendance> llist=session.createQuery("from LabAttendance s where s.student.roll=:id and s.student.division.name=:id1 and s.student.division.csClass.id=:id2").setParameter("id",rol).setParameter("id1",dname).setParameter("id2",clid).list();
            for(Iterator iterator = llist.iterator(); iterator.hasNext();)
            {
                LabAttendance subjectAttendance= (LabAttendance) iterator.next();
                session.delete(subjectAttendance);
            }
            session.delete(student);
            t.commit();
            session.close();
        }
//        t.commit();
        session.close();
        return "1";
    }
    @POST
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List  search(@FormParam("param1") String sname,@FormParam("param2")String div,@FormParam("param3")int clid)
    {
        Session session = Global.getSession();
        Transaction transaction=session.beginTransaction();
        try {

            java.util.List<Student> tlist=session.createQuery("from Student s where s.division.name=:id1 and s.division.csClass.id=:id2 and s.name like :id ").setParameter("id2",clid).setParameter("id","%"+sname+"%").setParameter("id1",div).list();
            List list=new ArrayList();
            for(Iterator iterator = tlist.iterator(); iterator.hasNext();)
            {
                Student student= (Student) iterator.next();
                List list1=new ArrayList();
                list1.add(student.getRoll());
                list1.add(student.getName());
                list1.add(student.getRoll());
                list.add(list1);
            }
            transaction.commit();
            session.close();
            return list;
        }
        catch (Exception e)
        {
//            transaction.commit();
            session.close();
            return null;
        }
    }
}