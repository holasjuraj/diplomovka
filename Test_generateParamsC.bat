@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
for %%c in (0 1 2) do (
    for %%t in (0.02 0.05 0.075 0.1 0.15 0.2) do (
        SET file=_params_files/C%%c_%%t.params
        echo Making !file!
        echo threads: 3 > !file!
        echo comparing-method: 0 >> !file!
        echo editdistance-early-stopping: 0.1 >> !file!
        echo qgram-size: 2 >> !file!
        echo scheduler: 1 >> !file!
        echo scheduler-tri-ineq--lb-minimum: %%t >> !file!
        echo scheduler-tri-ineq--min-bound-range: 0.001 >> !file!
        echo clustering-threshold: %%t >> !file!
        echo clustering-method: %%c >> !file!
        echo min-pattern-size: 1 >> !file!
    )
)
pause