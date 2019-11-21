using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace LMS.Controllers
{
    public class CommonController : Controller
    {

        /*******Begin code to modify********/

        protected Team2LMSContext db;

        public CommonController()
        {
            db = new Team2LMSContext();
        }


        /*
         * WARNING: This is the quick and easy way to make the controller
         *          use a different LibraryContext - good enough for our purposes.
         *          The "right" way is through Dependency Injection via the constructor 
         *          (look this up if interested).
        */

        public void UseLMSContext(Team2LMSContext ctx)
        {
            db = ctx;
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }




        /// <summary>
        /// Retreive a JSON array of all departments from the database.
        /// Each object in the array should have a field called "name" and "subject",
        /// where "name" is the department name and "subject" is the subject abbreviation.
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetDepartments()
        {
            var query =
            from d in db.Departments
            select new { name = d.Name, subject = d.Subject };

            foreach (var v in query)
            {
                System.Diagnostics.Debug.WriteLine(v);
            }

            return Json(query.ToArray());
        }



        /// <summary>
        /// Returns a JSON array representing the course catalog.
        /// Each object in the array should have the following fields:
        /// "subject": The subject abbreviation, (e.g. "CS")
        /// "dname": The department name, as in "Computer Science"
        /// "courses": An array of JSON objects representing the courses in the department.
        ///            Each field in this inner-array should have the following fields:
        ///            "number": The course number (e.g. 5530)
        ///            "cname": The course name (e.g. "Database Systems")
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetCatalog()
        {
            var q = from d in db.Departments
                    select new
                    {
                        subject = d.Subject,
                        dname = d.Name,
                        courses = (
                        from c in db.Courses
                        where c.Subject == d.Subject
                        select new
                        {
                            number = c.Num,
                            cname = c.Name
                        }
                        ).ToArray()
                    };

            return Json(q.ToArray());
        }

        /// <summary>
        /// Returns a JSON array of all class offerings of a specific course.
        /// Each object in the array should have the following fields:
        /// "season": the season part of the semester, such as "Fall"
        /// "year": the year part of the semester
        /// "location": the location of the class
        /// "start": the start time in format "hh:mm:ss"
        /// "end": the end time in format "hh:mm:ss"
        /// "fname": the first name of the professor
        /// "lname": the last name of the professor
        /// </summary>
        /// <param name="subject">The subject abbreviation, as in "CS"</param>
        /// <param name="number">The course number, as in 5530</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetClassOfferings(string subject, int number)
        {
            try
            {
                //TODO THE CATALOGID'S AREN'T MATCHING!!!!
                var query = from cours in db.Courses
                            where cours.Num == number && cours.Subject == subject
                            join clas in db.Classes
                            on cours.CatalogId equals clas.CatalogId
                            join p in db.Professors
                            on clas.ProfessorUId equals p.ProfessorUId
                            into cTable
                            from c in cTable
                            select new
                            {
                                season = clas.Semester,
                                year = clas.Year, //defaults to uint32, so extra conversion to string necessary 
                                location = clas.Loc,
                                start = clas.Start,
                                end = clas.End,
                                fname = c.FName,
                                lname = c.Name //this is actually the last name
                            };

                return Json(query.ToArray());
            }
            catch (InvalidCastException e)
            {
                Console.WriteLine(e.Message);
                return Json(null);
            }
            return Json(null);
        }

        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <returns>The assignment contents</returns>
        public IActionResult GetAssignmentContents(string subject, int num, string season, int year, string category, string asgname)
        {
            var query = from cours in db.Courses
                        where cours.Subject == subject && cours.Num == num
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where clas.Semester == season && clas.Year == year
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        where cat.Name == category
                        join a in db.Assignments
                        on cat.Name equals a.AcName
                        where a.Name == asgname
                        select a;

            return Content(query.FirstOrDefault().Contents); // grabs the first object in iteration
        }


        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment submission.
        /// Returns the empty string ("") if there is no submission.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <param name="uid">The uid of the student who submitted it</param>
        /// <returns>The submission text</returns>
        public IActionResult GetSubmissionText(string subject, int num, string season, int year, string category, string asgname,
            string uid)
        {
            int numUID = 0;
            if (uid.Contains('u')) numUID = Convert.ToInt32(uid.Substring(1));
            else numUID = Convert.ToInt32(uid); //get the int version of uid

            var query = from cours in db.Courses
                        where cours.Subject == subject && cours.Num == num
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where clas.Semester == season && clas.Year == year
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        where cat.Name == category
                        join a in db.Assignments
                        on cat.Name equals a.Name
                        where a.Name == asgname
                        join s in db.Submission
                        on a.AId equals s.AId
                        where s.StudentUId == numUID
                        select s;

            if (query.Count() == 0) return Content("");

            return Content(query.First().Contents);
        }


        /// <summary>
        /// Gets information about a user as a single JSON object.
        /// The object should have the following fields:
        /// "fname": the user's first name
        /// "lname": the user's last name
        /// "uid": the user's uid
        /// "department": (professors and students only) the name (such as "Computer Science") of the department for the user. 
        ///               If the user is a Professor, this is the department they work in.
        ///               If the user is a Student, this is the department they major in.    
        ///               If the user is an Administrator, this field is not present in the returned JSON
        /// </summary>
        /// <param name="uid">The ID of the user</param>
        /// <returns>
        /// The user JSON object 
        /// or an object containing {success: false} if the user doesn't exist
        /// </returns>
        public IActionResult GetUser(string uid)
        {
            int numUID = Convert.ToInt32(uid.Substring(1)); //get the int version of uid

            //check if the uID is in one of the other databases
            var studQuery = from s in db.Students
                            where s.StudentUId == numUID
                            join d in db.Departments
                            on s.Subject equals d.Subject
                            select new
                            {
                                fname = s.FName,
                                lname = s.LName,
                                uid = numUID,
                                department = d.Name
                            };

            if (studQuery.Count() != 0) return Json(studQuery.ToArray()[0]);

            var profQuery = from p in db.Professors
                            where p.ProfessorUId == numUID
                            join d in db.Departments
                            on p.Subject equals d.Subject
                            select new
                            {
                                fname = p.FName,
                                lname = p.Name, //actually the last name of the professor
                                uid = numUID,
                                department = d.Name
                            };

            if (profQuery.Count() != 0) return Json(profQuery.ToArray()[0]);

            var adminQuery = from a in db.Administrators
                             where a.AdministratorUId == numUID
                             select new
                             {
                                 fname = a.FName,
                                 lname = a.LName,
                                 uid = numUID
                             };

            if (adminQuery.Count() != 0) return Json(adminQuery.ToArray()[0]);

            return Json(new { success = false }); //default return if there are no results from any of the other queries.
        }




        /*******End code to modify********/

    }
}