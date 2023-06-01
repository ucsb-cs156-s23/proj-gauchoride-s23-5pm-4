import React from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import ShiftTable from "main/components/Shift/ShiftTable"

import { useBackend } from "main/utils/useBackend";
const ShiftPage = () => {

    const { data: users, error: _error, status: _status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            ["/api/admin/users"],
            // Stryker disable next-line StringLiteral,ObjectLiteral : since "GET" is default, "" is an equivalent mutation
            { method: "GET", url: "/api/admin/users" },
            []
        );

    return (
        <BasicLayout>
            {/* <h2>Shift</h2> */}
            <h2>Users</h2>
            <ShiftTable shift={users} />
        </BasicLayout>
    );
};

export default ShiftPage;
