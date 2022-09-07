$MinimalFields = Start-Job -ScriptBlock { artillery run minimal-fields.yaml }
$SomeFields = Start-Job -ScriptBlock { artillery run some-fields.yaml }
$AllFields = Start-Job -ScriptBlock { artillery run all-fields.yaml }

Wait-Job -Id $MinimalFields.Id, $SomeFields.Id, $AllFields.Id 

Get-job | Remove-Job

