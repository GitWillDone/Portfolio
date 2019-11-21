using System;
using System.Collections.Generic;


namespace LMS.Models.LMSModels
{
    public partial class Professors
    {
        public Professors()
        {
            Classes = new HashSet<Classes>();
        }

        public int ProfessorUId { get; set; }
        public string Subject { get; set; }
        public string FName { get; set; }
        public string Name { get; set; }
        public DateTime? Dob { get; set; }

        public virtual Users ProfessorU { get; set; }
        public virtual Departments SubjectNavigation { get; set; }
        public virtual ICollection<Classes> Classes { get; set; }
    }
}
