using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using LMS.Models.LMSModels;
using System.Diagnostics;

namespace LMS.Controllers
{
    [Authorize(Roles = "Professor")]
    public class ProfessorController : CommonController
    {
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Students(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
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

        public IActionResult Categories(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult CatAssignments(string subject, string num, string season, string year, string cat)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
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

        public IActionResult Submissions(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Grade(string subject, string num, string season, string year, string cat, string aname, string uid)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            ViewData["uid"] = uid;
            return View();
        }

        /*******Begin code to modify********/


        /// <summary>
        /// Returns a JSON array of all the students in a class.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "dob" - date of birth
        /// "grade" - the student's grade in this class
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetStudentsInClass(string subject, int num, string season, int year)
        {
            //start query by ensuring that we are getting the correct class, per the parameters
            var studentClassQuery = from clas in db.Classes
                                    where clas.Subject == subject && clas.Semester == season && clas.Year == year
                                    join e in db.Enrolled
                                    on clas.CId equals e.CId
                                    into cJoinE

                                    //if the class(es) are not empty, join the Student's table and pull their data 
                                    from cje in cJoinE.DefaultIfEmpty()
                                    join s in db.Students
                                    on cje.StudentUId equals s.StudentUId
                                    into join1
                                    from j in join1.DefaultIfEmpty()
                                    select new
                                    {
                                        fname = j.FName,
                                        lname = j.LName,
                                        uid = j.StudentUId,
                                        dob = j.Dob,
                                        grade = cje.Grade
                                    };
            //return the queried array, or null if it fails.
            try { return Json(studentClassQuery.ToArray()); }
            catch (Exception e) { return Json(null); }
        }



        /// <summary>
        /// Returns a JSON array with all the assignments in an assignment category for a class.
        /// If the "category" parameter is null, return all assignments in the class.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The assignment category name.
        /// "due" - The due DateTime
        /// "submissions" - The number of submissions to the assignment
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class, 
        /// or null to return assignments from all categories</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInCategory(string subject, int num, string season, int year, string category)
        {
            // Select all classes for this subject
            var query1 = from cours in db.Courses
                         join clas in db.Classes
                         on cours.CatalogId equals clas.CatalogId

                         // Select all classes for this subject matching the arguments passed to this method.
                         where cours.Subject == subject &&
                         cours.Num == num &&
                         clas.Semester == season &&
                         clas.Year == year

                         // Select all assignment categories for this class.
                         join cat in db.AssignmentCatagories
                         on clas.CId equals cat.CId

                         // Select all assignments for this class.
                         join a in db.Assignments
                         on cat.Name equals a.AcName

                         // A list of all assignments and their corresponding categories for this class.
                         select new
                         {
                             asg = a,
                             catag = cat
                         };

            JsonResult result; //will change depending on if the catagory exists or not

            if (category != null)
            {
                // Select only those assignments which belong to the given category.
                var query2 = from q in query1
                             where q.catag.Name == category
                             select new
                             {
                                 aname = q.asg.Name,
                                 cname = q.catag.Name,
                                 due = q.asg.Due,
                                 submissions = (from s in db.Submission
                                                where s.AId == q.asg.AId
                                                select s).Count()
                             };
                result = Json(query2.ToArray());
            }
            else
            {
                // Select all assignments - case where you don't have a category
                var query2 = from q in query1
                             select new
                             {
                                 aname = q.asg.Name,
                                 cname = q.catag.Name,
                                 due = q.asg.Due,
                                 submissions = (from s in db.Submission
                                                where s.AId == q.asg.AId
                                                select s).Count()
                             };
                result = Json(query2.ToArray());
            }
            try { return result; }
            catch (Exception e)
            {
                Console.WriteLine("Failed to get the assignments correctly.\n" + e.Message);
                return Json(null);
            }

            return Json(null); //default return - should not be reached
        }


        /// <summary>
        /// Returns a JSON array of the assignment categories for a certain class.
        /// Each object in the array should have the following fields:
        /// "name" - The category name
        /// "weight" - The category weight
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentCategories(string subject, int num, string season, int year)
        {
            // Join the course, classes, and assignment catagories to get teh 
            var query = from cours in db.Courses
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year
                        //TODO: add the defaultifempty() and temp tables later
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        select new { name = cat.Name, weight = cat.Weight };

            try { return Json(query.ToArray()); }
            catch (Exception e) { return Json(null); }
        }

        /// <summary>
        /// Creates a new assignment category for the specified class.
        /// If a category of the given class with the given name already exists, return success = false.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The new category name</param>
        /// <param name="catweight">The new category weight</param>
        /// <returns>A JSON object containing {success = true/false} </returns>
        public IActionResult CreateAssignmentCategory(string subject, int num, string season, int year, string category, int catweight)
        {
            // Grab the class id where the Courses matches the parameters (join Classes) to use for the CId in the AssignmentCatagory
            var query = from cours in db.Courses
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year
                        select clas.CId;

            //create the new AssignmentCatagory
            AssignmentCatagories asgCat = new AssignmentCatagories();
            asgCat.Name = category;
            asgCat.Weight = (uint)catweight;
            asgCat.CId = query.First();

            try
            {
                db.AssignmentCatagories.Add(asgCat);
                db.SaveChanges();
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                Console.WriteLine("Unable to add the Assignment Catagory.\n" + e.Message);
                return Json(new { success = false });
            }
        }

        /// <summary>
        /// Creates a new assignment for the given class and category.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="asgpoints">The max point value for the new assignment</param>
        /// <param name="asgdue">The due DateTime for the new assignment</param>
        /// <param name="asgcontents">The contents of the new assignment</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult CreateAssignment(string subject, int num, string season, int year, string category, string asgname,
            int asgpoints, DateTime asgdue, string asgcontents)
        {
            // Grab the appropriate Classes table based on the parameters
            var clasQuery = from cours in db.Courses
                         join clas in db.Classes
                         on cours.CatalogId equals clas.CatalogId
                         where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year
                         select clas;

            // Join with AssignmentCatagories to get the appropriate CId - TODO looks like you might've removed functionality here
            var catQuery = from q in clasQuery
                         join cat in db.AssignmentCatagories
                         on q.CId equals cat.CId
                         where cat.Name == category
                         select cat.CId;

            //create the assignment
            Assignments asg = new Assignments();
            asg.Name = asgname;
            asg.Contents = asgcontents;
            asg.Due = new DateTime(asgdue.Year, asgdue.Month, asgdue.Day, asgdue.Hour, asgdue.Minute, asgdue.Second);
            asg.Points = (uint)asgpoints;
            asg.AcName = category;

            // All students enrolled in this course - used to assign the Assignment to them
            var enrollQuery = from classes in clasQuery
                         join enrollments in db.Enrolled
                         on classes.CId equals enrollments.CId
                         select enrollments.StudentUId;

            try
            {
                //updates each student to have the assignment due
                foreach (int uid in enrollQuery)
                {
                    UpdateGrade(subject, num, season, year, uid);
                }
                db.Assignments.Add(asg);
                db.SaveChanges();
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                Console.WriteLine("Failure to create the Assignment.\n" + e.Message);
                return Json(new { success = false });
            }
        }


        /// <summary>
        /// Gets a JSON array of all the submissions to a certain assignment.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "time" - DateTime of the submission
        /// "score" - The score given to the submission
        /// 
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetSubmissionsToAssignment(string subject, int num, string season, int year, string category, string asgname)
        {
            //query for the relevant class and catagory per the input parameters
            var query = from cours in db.Courses
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year
                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        where cat.Name == category

                        //get the assignment based on the asgname parameter
                        join asg in db.Assignments
                        on cat.Name equals asg.AcName
                        where asg.Name == asgname

                        // grab the matching submission based on the AssignmentID
                        join sub in db.Submission
                        on asg.AId equals sub.AId

                        // Get the student's information
                        join stud in db.Students
                        on sub.StudentUId equals stud.StudentUId

                        select new
                        {
                            fname = stud.FName,
                            lname = stud.LName,
                            uid = stud.StudentUId,
                            time = sub.Time,
                            score = sub.Score
                        };

            try { return Json(query.ToArray()); }
            catch (Exception e) { return Json(null); }
        }


        /// <summary>
        /// Set the score of an assignment submission
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <param name="uid">The uid of the student who's submission is being graded</param>
        /// <param name="score">The new score for the submission</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult GradeSubmission(string subject, int num, string season, int year, string category, string asgname, 
            string uid, int score)
        {
            //convert the uid back into an int
            int iUID = Convert.ToInt32(uid);

            //query and update the submissions table
            //determine if the parameters match and relevant and appropriate fields match 
            //only the most recent submission will be used
            var subQuery = from cours in db.Courses
                        join clas in db.Classes
                        on cours.CatalogId equals clas.CatalogId
                        where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year

                        join cat in db.AssignmentCatagories
                        on clas.CId equals cat.CId
                        where cat.Name == category

                        join asg in db.Assignments
                        on cat.Name equals asg.AcName
                        where asg.Name == asgname

                        join sub in db.Submission
                        on asg.AId equals sub.AId
                        where sub.StudentUId == iUID
                        select sub;

            //update the student's grade; foreach to update
            subQuery.First().Score = (uint)score;

            try
            {
                db.SaveChanges(); //done twice to update new grade, then do the same after the method has been called to get new GPA
                UpdateGrade(subject, num, season, year, iUID); // Update class grade.
                db.SaveChanges();
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                Console.WriteLine("Failure to grade the submission.\n");
                return Json(new { success = false });
            }
        }


        /// <summary>
        /// Returns a JSON array of the classes taught by the specified professor
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester in which the class is taught
        /// "year" - The year part of the semester in which the class is taught
        /// </summary>
        /// <param name="uid">The professor's uid</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            int numUID = Convert.ToInt32(uid.Substring(1)); //get the int version of uid

            try
            {
                //get the classes based on the professors uID
                var clasQuery = from clas in db.Classes
                                join cours in db.Courses
                                on clas.CatalogId equals cours.CatalogId
                                where clas.ProfessorUId == numUID
                                select new
                                {
                                    subject = cours.Subject,
                                    number = cours.Num,
                                    name = cours.Name,
                                    season = clas.Semester,
                                    year = clas.Year //issue with converting to uint, even though it should be string
                                };
                return Json(clasQuery.ToArray());
            }
            catch (InvalidCastException e) { Console.WriteLine(e.Message); }
            
            return Json(null);

        }

        /// <summary>
        /// (HELPER METHOD)
        /// Recalculates the grade for the student - should only be used when changes to the grade or assignments creation is done
        /// </summary>
        /// <param name="subject"></param>
        /// <param name="num"></param>
        /// <param name="season"></param>
        /// <param name="year"></param>
        /// <param name="uid"></param>
        /// <returns>Success or Failure based on ability to save changes in db</returns>
        public IActionResult UpdateGrade(string subject, int num, string season, int year, int uid)
        {
            //variables to factor in the weight that the categories will have, and the numeric grade from scores/letter grades.
            double classWeight = 0;
            double numericGrade = 0;

            // grab the class relevant to the information passed in by the input parameters
            var classQuery = from clas in db.Classes
                         join cours in db.Courses
                         on clas.CatalogId equals cours.CatalogId
                         where cours.Subject == subject && cours.Num == num && clas.Semester == season && clas.Year == year
                         select clas;

            // Select all assignment categories for this class.
            var asgQuery = from classes in classQuery
                         join cat in db.AssignmentCatagories
                         on classes.CId equals cat.CId
                         select cat;

            //loop through each Assignment Catagory
            foreach (AssignmentCatagories cat in asgQuery)
            {
                //uints under the assumption that students cannot earn negative grades in a course
                uint catEarned = 0;
                uint catTotalPoints = 0;

                // Select all assignments for this category.
                var asgPerCatQuery = from categories in asgQuery
                             join asgs in db.Assignments
                             on categories.Name equals asgs.AcName
                             where asgs.AcName == cat.Name
                             select asgs;

                if (asgPerCatQuery.Count() == 0) continue;

                //loop through each assignment in the assignment catagory and get the total points
                foreach (Assignments asg in asgPerCatQuery)
                {
                    catTotalPoints += (uint)asg.Points;

                    // Select all submissions of this assignment for for the provided uID.
                    // This assumes there may only be a single submission per student per assignment.
                    var subQuery = from assignments in asgPerCatQuery
                                 join submissions in db.Submission
                                 on assignments.AId equals submissions.AId
                                 where submissions.StudentUId == uid && assignments.AId == asg.AId
                                 select submissions;

                    //the query should return either one result or none (no submission).
                    if (subQuery.Count() > 0) catEarned += (uint)subQuery.First().Score;
                }

                // For each assignment category do this
                double catPercent = (catEarned / (double)catTotalPoints);
                double catScore = catPercent * (double)cat.Weight;

                numericGrade += (int)catScore;
                classWeight += (double)cat.Weight;
            }

            numericGrade *= (100d / classWeight);

            // Select all enrollments for this class by the provided uID.
            var enrollQuery = from classes in classQuery
                         join enrollments in db.Enrolled
                         on classes.CId equals enrollments.CId
                         where enrollments.StudentUId == uid
                         select enrollments;

            enrollQuery.First().Grade = computeGrade((int)numericGrade);

            try
            {
                db.SaveChanges(); // Update the database with the new numeric grade.
                return Json(new { success = true });
            }
            catch (Exception e)
            {
                Console.WriteLine("Failure to update the student's grade.\n" + e.Message);
                return Json(new { success = false });
            }
        }

        /// <summary>
        /// computes the letter grade of a numeric grade passed in
        /// </summary>
        /// <param name="numGrade"></param>
        /// <returns>Letter grade version of numeric grade</returns>
        public string computeGrade(int numGrade)
        {
            string letterGrade = "";

            //letter grade is calculated per the University of Utah policy
            if (numGrade >= 93) letterGrade = "A";
            else if (numGrade >= 90) letterGrade = "A-";
            else if (numGrade >= 87) letterGrade = "B+";
            else if (numGrade >= 83) letterGrade = "B";
            else if (numGrade >= 80) letterGrade = "B-";
            else if (numGrade >= 77) letterGrade = "C+";
            else if (numGrade >= 73) letterGrade = "C";
            else if (numGrade >= 70) letterGrade = "C-";
            else if (numGrade >= 67) letterGrade = "D+";
            else if (numGrade >= 63) letterGrade = "D";
            else if (numGrade >= 60) letterGrade = "D-";
            else
                letterGrade = "F";

            return letterGrade;
        }

        /*******End code to modify********/

    }
}