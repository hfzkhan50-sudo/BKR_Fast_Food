using System;
using System.Diagnostics;
using System.IO;

internal static class Launcher
{
    private static void Main()
    {
        var appImage = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "output", "BKRFastFood");
        var appDirectory = Path.Combine(appImage, "app");
        var executable = Path.Combine(appImage, "BKRFastFood.exe");
        Process.Start(new ProcessStartInfo
        {
            FileName = executable,
            WorkingDirectory = appDirectory,
            UseShellExecute = true
        });
    }
}