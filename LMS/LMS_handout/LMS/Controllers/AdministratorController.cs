using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using LMS.Models.LMSModels;


namespace LMS.Controllers
{
    [Authorize(Roles = "Administrator")]
    public class AdministratorController : CommonController
    {
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Department(string subject)
        {
            ViewData["subject"] = subject;
            return View();
        }

        public IActionResult Course(string subject, string num)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Returns a JSON array of all the courses in the given department.
        /// Each object in the array should have the following fields:
        /// "number" - The course number (as in 5530)
        /// "name" - The course name (as in "Database Systems")
        /// </summary>
        /// <param name="subject">The department subject abbreviation (as in "CS")</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetCourses(string subject)
        {
            var query = from c in db.Courses
                        where c.Subject == subject
                        select new
                        {
                            number = c.Num,
                            name = c.Name
                        };
            try { return Json(query.ToArray()); }
            catch (Exception e) { return Json(null); }
        }

        /// <summary>
        /// Returns a JSON array of all the professors working in a given department.
        /// Each object in the array should have the following fields:
        /// "lname" - The professor's last name
        /// "fname" - The professor's first name
        /// "uid" - The professor's uid
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetProfessors(string subject)
        {
            var query = from p in db.Professors
                        where p.Subject == subject
                        select new
                        {
                            lname = p.Name,
                            fname = p.FName,
                            uid = p.ProfessorUId
                        };
            try { return Json(query.ToArray()); }
            catch (Exception e) { return Json(null); }
        }



        /// <summary>
        /// Creates a course.
        /// A course is uniquely identified by its number + the subject to which it belongs
        /// </summary>
        /// <param name="subject">The subject abbreviation for the department in which the course will be added</param>
        /// <param name="number">The course number</param>
        /// <param name="name">The course name</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the course already exists, true otherwise.</returns>
        public IActionResult CreateCourse(string subject, int number, string name)
        {
            var query = from c in db.Courses
                        where c.Subject == subject && c.Num == number && c.Name == name
                        select c;

            //find the greatest catalog id and increment by one, if one exists
            uint catID = 0;
            if (db.Courses.Count() == 0) catID = 0;
            else catID = db.Courses.Max(x => x.CatalogId) + 1; // thread safe due to locks done per command by default in Linq

            if (query.Count() == 0)
            {
                Courses course = new Courses();
                course.Subject = subject;
                course.Num = (uint)number;
                course.Name = name;
                course.CatalogId = catID;

                try
                {
                    db.Add(course);
                    db.SaveChanges();
                    return Json(new { success = true });
                }
                catch (Exception e)
                {
                    Console.WriteLine("Failure to update db.\n");
                    return Json(new { success = false });
                }
            }
            Console.WriteLine("An instance of the course already exists.\n");
            return Json(new { success = false });
        }



        /// <summary>
        /// Creates a class offering of a given course.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="number">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="start">The start time</param>
        /// <param name="end">The end time</param>
        /// <param name="location">The location</param>
        /// <param name="instructor">The uid of the professor</param>
        /// <returns>A JSON object containing {success = true/false}. 
        /// false if another class occupies the same location during any time 
        /// within the start-end range in the same semester, or if there is already
        /// a Class offering of the same Course in the same Semester,
        /// true otherwise.</returns>
        public IActionResult CreateClass(string subject, int number, string season, int year, DateTime start, DateTime end,
            string location, string instructor)
        {
            //the format of instructor is in all digits, so we'll do a direct conversion for it
            int numUID = Convert.ToInt32(instructor);
            var query = from cl in db.Classes
                        where cl.Subject == subject && cl.Semester == season && cl.Year == year && cl.Start == start
                        && cl.End == end && cl.Loc == location && cl.ProfessorUId == numUID
                        select cl;

            //get the catalog id from the course, using the subject
            var catidQuery = from cr in db.Courses
                             where cr.Subject == subject && cr.Num == number
                             select cr.CatalogId;

            if (query.Count() > 0)
            {
                Console.WriteLine("This instance of the class already exists.\n");
                return Json(new { success = false });
            }
            else
            {
                //get month and date based on Semester
                int month = 1;
                switch (season)
                {
                    case "Spring":
                        month = 1;
                        break;
                    case "Summer":
                        month = 5;
                        break;
                    case "Fall":
                        month = 8;
                        break;
                }

                //We're going to let MySql define the cID for us.
                Classes cls = new Classes();
                cls.CatalogId = catidQuery.First(); //grabbed from Courses.  If null is returned, the CatalogID doesn't exist.
                cls.Subject = subject;
                cls.Semester = season;
                cls.Year = (uint)year;
                cls.Loc = location;
                cls.ProfessorUId = numUID;
                cls.Start = new DateTime(year, month, 1, start.Hour, start.Minute, 1);
                cls.End = new DateTime(year, month + 3, 1, end.Hour, end.Minute, 1);
                try
                {
                    db.Classes.Add(cls);
                    db.SaveChanges();
                    return Json(new { success = true });
                }
                catch (Exception e)
                {
                    return Json(new { success = false });
                }
            }
        }

        /*******End code to modify********/

    }
}