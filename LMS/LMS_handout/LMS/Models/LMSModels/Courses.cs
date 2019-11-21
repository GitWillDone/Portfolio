using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Courses
    {
        public string Name { get; set; }
        public uint? Num { get; set; }
        public uint CatalogId { get; set; }
        public string Subject { get; set; }

        public virtual Departments SubjectNavigation { get; set; }
        public virtual Classes Classes { get; set; }
    }
}
