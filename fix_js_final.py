with open("src/main/resources/static/js/manager/revenue.js", "r", encoding="utf-8") as f:
    content = f.read()

# Fix 1: Remove spurious else-if donut blocks after export/print buttons
# These trigger because exportRevenueBtn/printReportBtn don't exist in HTML

import re

# Pattern 1: after export button - remove the else-if donut block
old1 = """        });
    } else if (donutSvg) {
        // Show placeholder when no data
        var msg = document.createElement('div');
        msg.style.textAlign = 'center';
        msg.style.padding = '40px 0';
        msg.style.color = 'var(--color-secondary)';
        msg.style.fontFamily = 'Inter, sans-serif';
        msg.style.fontSize = '13px';
        msg.textContent = 'No revenue data for selected filters';
        donutSvg.parentNode.appendChild(msg);
        if (donutLegend) donutLegend.innerHTML = '<div style="text-align:center;color:var(--color-secondary);font-size:13px;padding:10px">No data</div>';
    }

    var printBtn = document.getElementById('printReportBtn');"""

new1 = """        });

    var printBtn = document.getElementById('printReportBtn');"""

content = content.replace(old1, new1)

# Pattern 2: after print button - remove the else-if donut block  
old2 = """    } else if (donutSvg) {
        // Show placeholder when no data
        var msg = document.createElement('div');
        msg.style.textAlign = 'center';
        msg.style.padding = '40px 0';
        msg.style.color = 'var(--color-secondary)';
        msg.style.fontFamily = 'Inter, sans-serif';
        msg.style.fontSize = '13px';
        msg.textContent = 'No revenue data for selected filters';
        donutSvg.parentNode.appendChild(msg);
        if (donutLegend) donutLegend.innerHTML = '<div style="text-align:center;color:var(--color-secondary);font-size:13px;padding:10px">No data</div>';
    }
});"""

new2 = """    }
});"""

content = content.replace(old2, new2)

# Fix donut rendering
content = content.replace("var gapPct = 1.5;", "var gapPct = 3;")
content = content.replace('\n            circle.setAttribute("stroke-linecap", "round");', "")

with open("src/main/resources/static/js/manager/revenue.js", "w", encoding="utf-8") as f:
    f.write(content)

print("JS cleaned and fixed")
