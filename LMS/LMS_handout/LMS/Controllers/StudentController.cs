using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace LMS.Controllers
{
    [Authorize(Roles = "Student")]
    public class StudentController : CommonController
    {

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Catalog()
        {
            return View();
        }

        public IActionResult Class(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult Assignment(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }


        public IActionResult ClassListings(string subject, string num)
        {
            System.Diagnostics.Debug.WriteLine(subject + num);
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }


        /*******Begin code to modify********/

        /// <summary>
        /// Returns a JSON array of the classes the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester (Semester in our case)
        /// "year" - The year part of the semester
        /// "grade" - The grade earned in the class, or "--" if one hasn't been assigned
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            int numUID = Convert.ToInt32(uid.Substring(1)); //get the int version of uid

            var query = from s in db.Students
                        where s.StudentUId == numUID // to check
                        join e in db.Enrolled on s.StudentUId equals e.StudentUId
                        join clas in db.Classes on e.CId equals clas.CId
                        join cour in db.Courses on clas.CatalogId equals cour.CatalogId
                        select new
                        {
                            subject = clas.Subject,
                            number = cour.Num, 
                            name = cour.Name,
                            season = clas.Semester,
                            year = clas.Year,
                            grade = e.Grade == null ? "--" : e.Grade.ToString()
                        };
            return Json(query.ToArray());
        }

        /// <summary>
        /// Returns a JSON array of all the assignments in the given class that the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The category name that the assignment belongs to
        /// "due" - The due Date/Time
        /// "score" - The score earned by the student, or null if the student has not submitted to this assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="uid"></param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInClass(string subject, int num, string season, int year, string uid)
        {
            var query = from c in db.Courses
                        where c.Subject == subject && c.Num == num
                        join clas in db.Classes
                        on c.CatalogId equals clas.CatalogId
                        where clas.Semester == season && clas.Year == year
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        into courseClass

                        from cjc in courseClass.DefaultIfEmpty()
                        join a in db.Assignments
                        on cjc.Name equals a.AcName
                        into ccAssign

                        from ccja in ccAssign.DefaultIfEmpty()
                        join s in db.Submission
                        on new { A = Convert.ToString(ccja.AId), B = Convert.ToInt32(uid.Substring(1)) } //1 to slash the 'u'
                        equals new { A = Convert.ToString(s.AId), B = s.StudentUId }
                        into join1
                        from j in join1.DefaultIfEmpty()
                        select new
                        {
                            aname = ccja.Name,
                            cname = ccja.AcName,
                            due = ccja.Due,
                            score = j == null ? null : (int?)j.Score
                        };

            return Json(query.ToArray());
        }



        /// <summary>
        /// Adds a submission to the given assignment for the given student
        /// The submission should use the current time as its DateTime
        /// You can get the current time with DateTime.Now
        /// The score of the submission should start as 0 until a Professor grades it
        /// If a Student submits to an assignment again, it should replace the submission contents
        /// and the submission time (the score should remain the same).
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="uid">The student submitting the assignment</param>
        /// <param name="contents">The text contents of the student's submission</param>
        /// <returns>A JSON object containing {success = true/false}</returns>
        public IActionResult SubmitAssignmentText(string subject, int num, string season, int year,
          string category, string asgname, string uid, string contents)
        {
            //see if the assignment exists
            var query = from c in db.Courses
                        where c.Subject == subject && c.Num == num
                        join clas in db.Classes
                        on c.CatalogId equals clas.CatalogId
                        where clas.Semester == season && clas.Year == year //TODO we don't have a year and may have to add one
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        where cat.Name == category
                        join a in db.Assignments
                        on cat.Name equals a.AcName
                        where a.Name == asgname
                        select a;

            //see if this assigment exists
            if (query.Count() == 0) return Json(new { success = false });

            //get the first value - we'll be using this for submission and due date evaluation
            var firstAsg = query.First();

            //see if the assignment's due date has been exceeded
            if (DateTime.Now > firstAsg.Due)
            {
                Console.WriteLine("The due date has been exceeded.\n");
                return Json(new { success = false });
            }

            //see if the data has been exceeded.  If so, return with a message

            //convert the uid back into an int
            int iUID = Convert.ToInt32(uid.Substring(1));

            //create a Submission with the parameters 
            Submission sub = new Submission();
            sub.StudentUId = iUID;
            sub.AId = firstAsg.AId; //similar to Python .next to grab the next iterable object
            sub.Contents = contents;
            sub.Score = 0;
            sub.Time = DateTime.Now;

            //try and add the submission, or fail if not possible
            try
            {
                var check = from s in db.Submission
                            where s.AId == firstAsg.AId && s.StudentUId == iUID
                            select s;
                if (check.Count() == 0)
                {
                    db.Submission.Add(sub);
                }
                else
                {
                    db.Submission.Update(sub);
                }

                db.SaveChanges();
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                return Json(new { success = false });
            }
        }


        /// <summary>
        /// Enrolls a student in a class.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing {success = {true/false}. 
        /// false if the student is already enrolled in the class, true otherwise.</returns>
        public IActionResult Enroll(string subject, int num, string season, int year, string uid)
        {
            //convert the uid back into an int
            int iUID = Convert.ToInt32(uid.Substring(1));
            var query = from c in db.Courses
                        where c.Subject == subject && c.Num == num
                        join clas in db.Classes
                        on c.CatalogId equals clas.CatalogId
                        where clas.Semester == season && clas.Year == year
                        select clas;

            //check if the class exists

            if (query.Count() == 0) return Json(new { success = false });

            //create an Enrolled object
            Enrolled enroll = new Enrolled();
            enroll.CId = query.First().CId;
            enroll.StudentUId = iUID;
            enroll.Grade = "--";

            //try and add it to the db
            try
            {
                db.Enrolled.Add(enroll);
                db.SaveChanges();
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                return Json(new { success = false });
            }
        }



        /// <summary>
        /// Calculates a student's GPA
        /// A student's GPA is determined by the grade-point representation of the average grade in all their classes.
        /// Assume all classes are 4 credit hours.
        /// If a student does not have a grade in a class ("--"), that class is not counted in the average.
        /// If a student is not enrolled in any classes, they have a GPA of 0.0.
        /// Otherwise, the point-value of a letter grade is determined by the table on this page:
        /// https://advising.utah.edu/academic-standards/gpa-calculator-new.php
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing a single field called "gpa" with the number value</returns>
        public IActionResult GetGPA(string uid)
        {

            //convert the uid back into an int
            int iUID = Convert.ToInt32(uid.Substring(1));

            //get the grades for all classes
            var query = from e in db.Enrolled
                        where e.StudentUId == iUID
                        select e.Grade;

            int classCount = 0;
            double GPA = 0;

            foreach (var grade in query)
            {
                if (grade == "--") continue; //the user doesn't have a grade in the class

                classCount++;

                if (grade.Equals("A")) GPA += 4.0;
                else if (grade.Equals("A")) GPA += 3.7;
                else if (grade.Equals("A-")) GPA += 3.3;
                else if (grade.Equals("B+")) GPA += 3.0;
                else if (grade.Equals("B")) GPA += 2.7;
                else if (grade.Equals("B-")) GPA += 2.3;
                else if (grade.Equals("C+")) GPA += 2.0;
                else if (grade.Equals("C")) GPA += 1.7;
                else if (grade.Equals("C-")) GPA += 1.3;
                else if (grade.Equals("D+")) GPA += 1.0;
                else if (grade.Equals("D")) GPA += 0.7;
                else
                    GPA += 0;
            }
            return Json(new { gpa = GPA / (double)classCount });
        }

        /*******End code to modify********/

    }
}