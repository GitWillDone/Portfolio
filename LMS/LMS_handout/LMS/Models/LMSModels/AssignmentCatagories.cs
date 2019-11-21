using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class AssignmentCatagories
    {
        public string Name { get; set; }
        public uint? Weight { get; set; }
        public string CId { get; set; }

        public virtual Classes C { get; set; }
    }
}
