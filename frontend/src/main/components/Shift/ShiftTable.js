
import OurTable, { ButtonColumn } from "main/components/OurTable"
import { useBackendMutation } from "main/utils/useBackend";



export default function ShiftTable({ shift }) {

    function cellToAxiosParamsToggleAdmin(cell) {
        return {
            url: "/api/admin/users/toggleAdmin",
            method: "POST",
            params: {
                id: cell.row.values.id
            }
        }
    }

    // Stryker disable all : hard to test for query caching
    const toggleAdminMutation = useBackendMutation(
        cellToAxiosParamsToggleAdmin,
        {},
        ["/api/admin/users"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const toggleAdminCallback = async (cell) => { toggleAdminMutation.mutate(cell); }


    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },
        {
            Header: 'day',
            accessor: 'day',
        },
        {
            Header: 'shift',
            accessor: 'shift',
        },
        {
            Header: 'driver',
            accessor: 'driver'
        },
        {
            Header: 'driver backup',
            accessor: 'driverBackup',
        }
    ];

    const buttonColumn = [
        ...columns,
        ButtonColumn("toggle-admin", "primary", toggleAdminCallback, "ShiftTable"),
    ]

    //const columnsToDisplay = showButtons ? buttonColumn : columns;

    return <OurTable
        data={shift}
        columns={buttonColumn}
        testid={"ShiftTable"}
    />;
};