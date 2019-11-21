using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Classes
    {
        public Classes()
        {
            AssignmentCatagories = new HashSet<AssignmentCatagories>();
            Enrolled = new HashSet<Enrolled>();
        }

        public string CId { get; set; }
        public string Loc { get; set; }
        public DateTime? Start { get; set; }
        public DateTime? End { get; set; }
        public string Semester { get; set; }
        public uint CatalogId { get; set; }
        public int? ProfessorUId { get; set; }
        public string Subject { get; set; }
        public uint Year { get; set; }

        public virtual Courses Catalog { get; set; }
        public virtual Professors ProfessorU { get; set; }
        public virtual Departments SubjectNavigation { get; set; }
        public virtual ICollection<AssignmentCatagories> AssignmentCatagories { get; set; }
        public virtual ICollection<Enrolled> Enrolled { get; set; }
    }
}
