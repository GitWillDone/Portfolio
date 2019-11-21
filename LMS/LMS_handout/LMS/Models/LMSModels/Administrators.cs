using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Administrators
    {
        public int AdministratorUId { get; set; }
        public string FName { get; set; }
        public string LName { get; set; }
        public DateTime? Dob { get; set; }

        public virtual Users AdministratorU { get; set; }
    }
}
