using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Students
    {
        public Students()
        {
            Enrolled = new HashSet<Enrolled>();
            Submission = new HashSet<Submission>();
        }

        public int StudentUId { get; set; }
        public string Subject { get; set; }
        public string FName { get; set; }
        public string LName { get; set; }
        public DateTime? Dob { get; set; }

        public virtual Users StudentU { get; set; }
        public virtual Departments SubjectNavigation { get; set; }
        public virtual ICollection<Enrolled> Enrolled { get; set; }
        public virtual ICollection<Submission> Submission { get; set; }
    }
}
